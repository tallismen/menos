package nl.anwb.hv.menos.controller;

import nl.anwb.hv.menos.client.JmsClient;
import nl.anwb.hv.menos.models.NoodoproepTelefoon;
import nl.anwb.hv.menos.util.NoodoproepProces;
import org.anwb.hv.ict.oxi.disweg.Noodoproep;
import org.anwb.hv.ict.oxi.wam.SendRequest;
import org.anwb.hv.oxi3.util.Oxi3JaxbContext;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.SequenceInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Deze class is de controller van de verschillende links
 */
@RestController
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
//    private final String realPathtoUploads = "C:" + File.separator + "Users" + File.separator + "p285143" + File.separator + "uploads" + File.separator;

    @Autowired
    private JmsClient jmsClient;

    @RequestMapping(value = "/audio", method = RequestMethod.GET)
    public File streamAudio(HttpServletRequest request, HttpServletResponse response) {
        String uploadsDir = "/uploads/";
        String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
        File dir = new File(realPathtoUploads);
        File[] files = dir.listFiles();
        File compleet = new File(realPathtoUploads + "noodoproep.wav");
        if (files != null) {
            for (File file : files) {
                try {
                    AudioInputStream clip1 = AudioSystem.getAudioInputStream(compleet);
                    AudioInputStream clip2 = AudioSystem.getAudioInputStream(file);

                    AudioInputStream appendfiles = new AudioInputStream(new SequenceInputStream(clip1, clip2),
                            clip1.getFormat(), clip1.getFrameLength() + clip2.getFrameLength());
                    AudioSystem.write(appendfiles, AudioFileFormat.Type.WAVE, compleet);
                } catch (Exception e) {
                    logger.error("Er is iets fout gegaan! " + e);
                }
            }
        }
        return compleet;
    }

    /**
     * Geeft alle audiofiles op de server
     * @param response zip file met audio files
     */
    @RequestMapping(value = "/audiofiles", produces = "application/zip")
    public String  zipFiles(HttpServletResponse response, HttpServletRequest request) {
        response.setStatus(HttpServletResponse.SC_OK);
        String uploadsDir = "/uploads/";
        String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
        File dir = new File(realPathtoUploads);
        File[] files = dir.listFiles();

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            for (File file : files) {
                if(file.getName().contains(getCurrentTimeStamp())) {
                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                    FileInputStream fileInputStream = new FileInputStream(file);

                    IOUtils.copy(fileInputStream, zipOutputStream);

                    fileInputStream.close();
                    zipOutputStream.closeEntry();
                }
            }

            zipOutputStream.close();
        } catch (Exception e) {
            logger.error("Er is iets fout gegaan! " + e);
        }
        return "<a id=\"link_button\" href=\"\" target=\"_self\"/>Terug</a>";
    }

    @RequestMapping(value = "/audiostream", method = RequestMethod.GET)
    public void listenAudio(HttpServletResponse response, HttpServletRequest request) {
        logger.info("/audiostream aangeroepen");
        try {
            String uploadsDir = "/uploads/";
            String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
            File directory = new File(realPathtoUploads);
            File[] files = directory.listFiles();
            Arrays.sort(files);

            File file1 = files[0];
            for (File file : files) {

//                response.getOutputStream().write(readContentIntoByteArray(file));
            }
            response.getOutputStream().write(readContentIntoByteArray(file1));
        } catch (Exception e) {
            logger.error("Iets fout gegaan! " + e);
        }
    }

    private static byte[] readContentIntoByteArray(File file) {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bFile;
    }

    @RequestMapping(value = "/uploadlist", method = RequestMethod.GET)
    public @ResponseBody ArrayList<String> getUploadlist(HttpServletRequest request) {
        logger.info("/uploadlist aangeroepen");
        ArrayList<String> uploadlist = new ArrayList<String>();
        try {
            String uploadsDir = "/uploads/";
            String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
            logger.info("realPathtoUploads = {}", realPathtoUploads);
            if (!new File(realPathtoUploads).exists()) {
                new File(realPathtoUploads).mkdir();
            } else {
                File directory = new File(realPathtoUploads);
                File[] files = directory.listFiles();
                for (File file : files) {
                    uploadlist.add(file.getName());
                }
            }
        } catch (Exception e) {
            logger.info("Directory niet aangemaakt of gevonden! " + e);
        }
        return uploadlist;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity uploadFile(@RequestParam(value = "file") MultipartFile multipartFile, HttpServletRequest request) {
        logger.info("/upload aangeroepen");
        if (!multipartFile.isEmpty()) {
            logger.info("Size:" + multipartFile.getSize());
        }
        try {
            String uploadsDir = "/uploads/";
            String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
            if (!new File(realPathtoUploads).exists()) {
                new File(realPathtoUploads).mkdir();
            }

            logger.info("realPathtoUploads = {}", realPathtoUploads);


            String orgName = multipartFile.getOriginalFilename();
            String filePath = realPathtoUploads + orgName;
            File dest = new File(filePath);
            multipartFile.transferTo(dest);
        } catch (Exception e) {
            logger.info("Fout! " + e);
        }
        return new ResponseEntity<String>("{}", HttpStatus.OK);
    }

    @RequestMapping(value = "/testConnectie", method = RequestMethod.GET)
    public String testConnectie() {
        logger.info("/testConnectie aangeroepen");
        return "gelukt";
    }

    /**
     * @ResponseBody Convert JSON object naar Java object
     * @RequestBody Convert Java object naar JSON
     */
    @RequestMapping(value = "/noodMelding", method = RequestMethod.POST)
    public
    @ResponseBody
    NoodoproepTelefoon noodmeldingOntvangen(@RequestBody NoodoproepTelefoon noodoproepTelefoon) {
        logger.info("/noodMelding aangeroepen: {}", noodoproepTelefoon);
        boolean verzonden = false;
        verzonden = sendNoodoproepToBroker(noodoproepTelefoon);
        //TODO: werkt niet!!!
//        if(verzonden){
//            return noodoproepTelefoon;
//        }else {
//            return null;
//        }
        return noodoproepTelefoon;
    }

    /**
     * Deze methode maakt van een NoodoproepTelefoon een Noodoproep.
     * Er wordt een sendrequest gemaakt(Noodoproep naar XML) met mannr en noodoproep.
     * Bericht wordt verzonden, sendRequest wordt ook naar XML gezet.
     *
     * @return Boolean of het gelukt is.
     */

    private boolean sendNoodoproepToBroker(NoodoproepTelefoon noodoproepTelefoon) {
        logger.info("Noodoproep versturen...");
        Noodoproep noodoproep = NoodoproepProces.noodoproepTelefoonToNoodoproep(noodoproepTelefoon);
        Boolean verzonden = false;
        try {
            SendRequest sendRequest = NoodoproepProces.makeSendRequest(noodoproepTelefoon.getmannr(), noodoproep);
            Object replay;
            replay = jmsClient.send(Oxi3JaxbContext.marshall(sendRequest, true));
            verzonden = true;
        } catch (Exception e) {
            logger.error("Noopdoproep versturen gefaald: {}", e.getMessage(), e);
        }
        logger.info("Verzonden: {}", verzonden);
        return verzonden;
    }

    /**
     * Haalt de huidige tijd op.
     *
     * @return huidige tijd
     */
    private static String getCurrentTimeStamp() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("-MM-dd-");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            logger.info("Timestamp: " + currentDateTime);
            return currentDateTime;
        } catch (Exception e) {
            logger.error("Timestamp fout gegaan!" + e);
            return null;
        }
    }
}
