package com.epsl.peritos.backend;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.epsl.peritos.backend.OfyService.ofy;


/**
 * Created by David Miguel Poyatos Reina on 22/5/16.
 */


public class Upload extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private static Storage storageService;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "peritoslapp";
    private static final String BUCKET_NAME = "peritosapp.appspot.com";


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<BlobKey> blobs = blobstoreService.getUploads(req).get("file");
        BlobKey blob = blobs.get(0);
        BlobstoreInputStream in = new BlobstoreInputStream(blob);

        String fname = "contenidos.txt";

        try {
            uploadStream(fname, "text/plain", in, BUCKET_NAME);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        blobstoreService.delete(blob);


        String servingUrl = "http://storage.googleapis.com/" + BUCKET_NAME + "/" + fname;


        //Obtener la lista de terminales para enviar URL de actualización

        List<Usuarios> usrs = ofy().load().type(Usuarios.class).list();
        Messaging wm = new Messaging();
        for (Usuarios usr : usrs) {
            String gcmid = usr.getRegId().toString();
            String message = "UPDATE#"+servingUrl;
            try {
                wm.sendMessage(message,gcmid);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        try {
            //Devolver respuesta
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json");

            // Initialize JSON response.
            JSONObject jsonResponse = new JSONObject();
            JSONArray jsonFiles = new JSONArray();
            jsonResponse.put("files", jsonFiles);

                JSONObject jsonFile = new JSONObject();
                jsonFiles.put(jsonFile);

                jsonFile.put("name", "¡Archivo actualizado correctamente!   -   URL: "+servingUrl);
                jsonFile.put("size", "13000");

            // Write JSON response.
            PrintWriter outt = res.getWriter();
            outt.print(jsonResponse.toString());
            outt.flush();
            outt.close();
        } catch (IOException | JSONException e) {
        }

    }



    /**
    * Returns an authenticated Storage object used to make service calls to Cloud Storage.
    */
    private static Storage getService() throws IOException, GeneralSecurityException {
        if (null == storageService) {
            GoogleCredential credential = GoogleCredential.getApplicationDefault();
            // Depending on the environment that provides the default credentials (e.g. Compute Engine,
            // App Engine), the credentials may require us to specify the scopes we need explicitly.
            // Check for this case, and inject the Cloud Storage scope if required.
            if (credential.createScopedRequired()) {
                credential = credential.createScoped(StorageScopes.all());
            }
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            storageService = new Storage.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
        }
        return storageService;
    }

    /**
     * Fetches the metadata for the given bucket.
     *
     * @param bucketName the name of the bucket to get metadata about.
     * @return a Bucket containing the bucket's metadata.
     */
    public static Bucket getBucket(String bucketName) throws IOException, GeneralSecurityException {
        Storage client = getService();
        Storage.Buckets.Get bucketRequest = client.buckets().get(bucketName);
        // Fetch the full set of the bucket's properties (e.g. include the ACLs in the response)
        bucketRequest.setProjection("full");
        return bucketRequest.execute();
    }


    /**
     * Fetch a list of the objects within the given bucket.
     *
     * @param bucketName the name of the bucket to list.
     * @return a list of the contents of the specified bucket.
     */
    public static List<StorageObject> listBucket(String bucketName)
            throws IOException, GeneralSecurityException {
        Storage client = getService();
        Storage.Objects.List listRequest = client.objects().list(bucketName);

        List<StorageObject> results = new ArrayList<StorageObject>();
        Objects objects;

        // Iterate through each page of results, and add them to our results list.
        do {
            objects = listRequest.execute();
            // Add the items in this page of results to the list we'll return.
            results.addAll(objects.getItems());

            // Get the next page, in the next iteration of this loop.
            listRequest.setPageToken(objects.getNextPageToken());
        } while (null != objects.getNextPageToken());

        return results;
    }

    /**
     * Uploads data to an object in a bucket.
     *
     * @param name the name of the destination object.
     * @param contentType the MIME type of the data.
     * @param stream the data - for instance, you can use a FileInputStream to upload a file.
     * @param bucketName the name of the bucket to create the object in.
     */
    public static void uploadStream(String name, String contentType, InputStream stream, String bucketName) throws IOException, GeneralSecurityException {
        InputStreamContent contentStream = new InputStreamContent(contentType, stream);
        StorageObject objectMetadata = new StorageObject()
                // Set the destination object name
                .setName(name)// Set the access control list to publicly read-only
                .setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        // Do the insert
        Storage client = getService();
        Storage.Objects.Insert insertRequest = client.objects().insert(bucketName, objectMetadata, contentStream);
        insertRequest.execute();
    }

    /**
     * Deletes an object in a bucket.
     *
     * @param path the path to the object to delete.
     * @param bucketName the bucket the object is contained in.
     */
    public static void deleteObject(String path, String bucketName)
            throws IOException, GeneralSecurityException {
        Storage client = getService();
        client.objects().delete(bucketName, path).execute();
    }

}
