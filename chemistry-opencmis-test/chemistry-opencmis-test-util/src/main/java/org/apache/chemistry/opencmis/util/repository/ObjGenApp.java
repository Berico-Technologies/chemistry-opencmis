/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.chemistry.opencmis.util.repository;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.spi.CmisBinding;
import org.apache.chemistry.opencmis.commons.spi.RepositoryService;
import org.apache.chemistry.opencmis.util.repository.ObjectGenerator.CONTENT_KIND;

public class ObjGenApp {

    public static final String DEFAULT_USER = "";
    public static final String DEFAULT_PWD = "";
    public static final String PROP_ATOMPUB_URL = "opencmis.test.atompub.url";
    public static final String PROP_URL = "url";
    public static final String PROP_WS_URL = "opencmis.test.webservices.url";
    public static final String DEFAULT_ATOMPUB_URL = "http://localhost:8080/opencmis/atom";
    public static final String DEFAULT_WS_URL = "http://localhost:8080/cmis/services/";

    private static final String CMD = "Command";
    private static final String REPOSITORY_ID = "RepositoryId";
    private static final String FILLER_DOCUMENT_TYPE_ID = "DocumentTypeId";
    private static final String FILLER_FOLDER_TYPE_ID = "FolderTypeId";
    private static final String FILLER_DOCS_PER_FOLDER = "DocsPerFolder";
    private static final String FILLER_FOLDERS_PER_FOLDER = "FoldersPerFolder";
    private static final String FILLER_DEPTH = "Depth";
    private static final String FILLER_CONTENT_SIZE = "ContentSizeInKB";
    private static final String COUNT = "Count";
    private static final String BINDING = "Binding";
    private static final String CLEANUP = "Cleanup";
    private static final String ROOTFOLDER = "RootFolder";
    private static final String THREADS = "Threads";
    private static final String CONTENT_KIND = "ContentKind";
    private static final String FILE_NAME_PATTERN = "FileName";
//    private static final String FILE = "File";

    private static final String BINDING_ATOM = "AtomPub";
    private static final String BINDING_WS = "WebService";

    private CmisBinding binding;
    private boolean fUsingAtom;
    private String fUrlStr;
    private CONTENT_KIND fContentKind;

    OptionSpec<String> fCmd;
    OptionSpec<Integer> fDepth;
    OptionSpec<Integer> fContentSize;
    OptionSpec<Integer> fFolderPerFolder;
    OptionSpec<Integer> fDocsPerFolder;
    OptionSpec<String> fFolderType;
    OptionSpec<String> fDocType;
    OptionSpec<String> fRepoId;
    OptionSpec<Integer> fCount;
    OptionSpec<String> fBinding;
    OptionSpec<Boolean> fCleanup;
    OptionSpec<String> fRootFolder;
    OptionSpec<Integer> fThreads;
    OptionSpec<String> fFileName;
    OptionSpec<String> fContentKindStr;
    OptionSpec<String> fFileNamePattern;
    
    public static void main(String[] args) {

        ObjGenApp app = new ObjGenApp();
        try {
            app.processCmdLine(args);
        } catch (CmisBaseException ce) {
            System.out.println("Error: Could not process command. " + ce);
            System.out.println("Extended error: " + ce.getErrorContent());
            ce.printStackTrace();
        } catch (Exception e) {
            System.out.println("Could not fill repository " + e);
            e.printStackTrace();
        }
    }

    private void processCmdLine(String[] args) {

        OptionParser parser = new OptionParser();
        fCmd = parser.accepts(CMD).withRequiredArg().describedAs("Command to perform (see below)");
        fRepoId = parser.accepts(REPOSITORY_ID).withOptionalArg().describedAs("Repository used");
        fDocType = parser.accepts(FILLER_DOCUMENT_TYPE_ID).withOptionalArg().defaultsTo(
                BaseTypeId.CMIS_DOCUMENT.value()).describedAs("Document type created");
        fFolderType = parser.accepts(FILLER_FOLDER_TYPE_ID).withOptionalArg()
                .defaultsTo(BaseTypeId.CMIS_FOLDER.value()).describedAs("Folder type created");
        fDocsPerFolder = parser.accepts(FILLER_DOCS_PER_FOLDER).withOptionalArg().ofType(Integer.class).describedAs(
                "Documents on each level").defaultsTo(1);
        fFolderPerFolder = parser.accepts(FILLER_FOLDERS_PER_FOLDER).withOptionalArg().ofType(Integer.class)
                .describedAs(" Folders on each level").defaultsTo(0);
        fDepth = parser.accepts(FILLER_DEPTH).withOptionalArg().ofType(Integer.class).describedAs("Levels of folders")
                .defaultsTo(1);
        fContentSize = parser.accepts(FILLER_CONTENT_SIZE).withOptionalArg().ofType(Integer.class).describedAs(
                "Content size of each doc").defaultsTo(0);
        fCount = parser.accepts(COUNT).withOptionalArg().ofType(Integer.class).defaultsTo(1).describedAs(
                "Repeat a command n times (partially implemented)");
        fBinding = parser.accepts(BINDING).withOptionalArg().ofType(String.class).defaultsTo(BINDING_ATOM).describedAs(
                "Protocol Binding: " + BINDING_ATOM + " or " + BINDING_WS);
        fCleanup = parser.accepts(CLEANUP).withOptionalArg().ofType(Boolean.class).defaultsTo(false).describedAs(
                "Clean all created objects at the end");
        fRootFolder = parser.accepts(ROOTFOLDER).withOptionalArg().ofType(String.class).describedAs(
                "folder id used as root to create objects (default repository root folder)");
        fThreads = parser.accepts(THREADS).withOptionalArg().ofType(Integer.class).defaultsTo(1).describedAs(
                "Number of threads to start in parallel");
//        fFileName = parser.accepts(FILE).withRequiredArg().ofType(String.class).describedAs("Input File");
        fContentKindStr = parser.accepts(CONTENT_KIND).withOptionalArg().ofType(String.class).defaultsTo("lorem/text")
                .describedAs("kind of content: static/text, lorem/text, lorem/html, fractal/jpeg");
        fFileNamePattern = parser.accepts(FILE_NAME_PATTERN).withOptionalArg().ofType(String.class).defaultsTo("ContentData-%03d.bin")
                .describedAs("file name pattern to be used with CreateFiles action");
        
        OptionSet options = parser.parse(args);

        if (options.valueOf(fCmd) == null || options.has("?")) {
            usage(parser);
        }

        if (options.valueOf(fBinding).equals(BINDING_WS)) {
            fUsingAtom = false;
        } else if (options.valueOf(fBinding).equals(BINDING_ATOM)) {
            fUsingAtom = true;
        } else {
            System.out.println("Unknown option <Binding>: " + options.valueOf(fBinding) + " allowed values: "
                    + BINDING_WS + " or " + BINDING_ATOM);
            return;
        }

        String kind = options.valueOf(fContentKindStr);
        if (null == kind) {
            if (options.valueOf(fContentSize) > 0)
                fContentKind = ObjectGenerator.CONTENT_KIND.StaticText;
            else
                fContentKind = null;
        } if (kind.equals("static/text"))
            fContentKind = ObjectGenerator.CONTENT_KIND.StaticText;
        else if (kind.equals("lorem/text"))
            fContentKind = ObjectGenerator.CONTENT_KIND.LoremIpsumText;
        else if (kind.equals("lorem/html"))
            fContentKind = ObjectGenerator.CONTENT_KIND.LoremIpsumHtml;
        else if (kind.equals("fractal/jpeg"))
            fContentKind = ObjectGenerator.CONTENT_KIND.ImageFractalJpeg;
        else {
            System.out.println("Unknown content kind: " + options.valueOf(fContentKindStr));
            System.out.println("  must be one of static/text, lorem/text, lorem/html, fractal/jpeg");
            usage(parser);
        }

        if (null == options.valueOf(fCmd)) {
            System.out.println("No command given.");
            usage(parser);
        } else if (options.valueOf(fCmd).equals("FillRepository")) {
            fillRepository(options);
        } else if (options.valueOf(fCmd).equals("CreateDocument")) {
            createSingleDocument(options);
        } else if (options.valueOf(fCmd).equals("CreateFolder")) {
            createFolders(options);
        } else if (options.valueOf(fCmd).equals("RepositoryInfo")) {
            repositoryInfo(options);
//        } else if (options.valueOf(fCmd).equals("CreateTypes")) {
//            createTypes(options);
        } else if (options.valueOf(fCmd).equals("CreateFiles")) {
            createFiles(options);
        } else if (options.valueOf(fCmd).equals("GetUrl")) {
            getUrl(getConfiguredUrl());
        } else {
            System.out.println("Unknown cmd: " + options.valueOf(fCmd));
            usage(parser);
        }
    }

    // private void preInitExpensiveTasks() {
    // // JAXB initialization is very expensive, count this separate:
    // TimeLogger logger = new TimeLogger("Initialization");
    // logger.start();
    // try {
    // JaxBHelper.createMarshaller();
    // }
    // catch (JAXBException e) {
    // System.out.print("Failuer in JAXB init: " + e);
    // e.printStackTrace();
    // } // dummy call just to get initialized
    // logger.stop();
    // logger.printTimes();
    // }

    private static void usage(OptionParser parser) {
        try {
            System.out.println();
            System.out.println("ObjGenApp is a command line tool for testing a CMIS repository.");
            System.out.println("Usage:");
            parser.printHelpOn(System.out);
            System.out.println();
            System.out.println("Command is one of [CreateDocument, CreateFolder, FillRepository, RepositoryInfo, CreateFiles]");
            System.out.println("JVM system properties: " + PROP_ATOMPUB_URL + ", " + PROP_WS_URL);
            System.out.println();
            System.out.println("Example: ");
            System.out.println("java -D"
                    + PROP_ATOMPUB_URL
                    + "=http://localhost:8080/opencmis/atom -cp ... "
                    + "org.apache.chemistry.opencmis.util.repository.ObjGenApp --Binding=AtomPub --Command=CreateDocument "
                    + "--RepositoryId=A1 --ContentSizeInKB=25 --ContentKind=lorem/text");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillRepository(String repoId, int docsPerFolder, int foldersPerFolders, int depth,
            String documentType, String folderType, int contentSizeInKB, String rootFolderId, boolean doCleanup) {

        MultiThreadedObjectGenerator.ObjectGeneratorRunner runner = MultiThreadedObjectGenerator.prepareForCreateTree(
                getBinding(), repoId, docsPerFolder, foldersPerFolders, depth, documentType, folderType,
                contentSizeInKB, rootFolderId, fContentKind, doCleanup);
        ObjectGenerator gen = runner.getObjectGenerator();
        runner.doCreateTree();

        System.out.println();
        System.out.println("Result:");
        System.out.println("Filling repository succeeded.");
        System.out.println("Folder used as root for creation (null=rootFolderId): " + rootFolderId);
        System.out.println("Number of documents created: " + gen.getDocumentsInTotal());
        System.out.println("Number of folders created: " + gen.getFoldersInTotal());
        gen.printTimings();
    }

    private void fillRepositoryMT(int noThreads, String repoId, int docsPerFolder, int foldersPerFolders, int depth,
            String documentType, String folderType, int contentSizeInKB, String rootFolderId, boolean doCleanup) {

        // Step 1: create a root folder for each thread
        MultiThreadedObjectGenerator.ObjectGeneratorRunner runner = MultiThreadedObjectGenerator
                .prepareForCreateFolder(getBinding(), repoId, folderType, rootFolderId, noThreads, doCleanup);
        String[] folderIds = runner.doCreateFolder();

        // Step 2: fill each root folder with an object tree
        MultiThreadedObjectGenerator.ObjectGeneratorRunner[] runners = MultiThreadedObjectGenerator
                .prepareForCreateTreeMT(getBinding(), repoId, docsPerFolder, foldersPerFolders, depth, documentType,
                        folderType, contentSizeInKB, folderIds, fContentKind, doCleanup);

        MultiThreadedObjectGenerator.runMultiThreaded(runners);
        System.out.println("Filling repository succeeded.");
    }

    private void printParameters(OptionSet options) {
        if (fUsingAtom) {
            System.out.println("Using AtomPub, connecting to  " + getAtomPubUrl());
        } else {
            System.out.println("Using WebService, connecting to  " + getWsUrl());
        }

        System.out.println("Repository id is: " + options.valueOf(fRepoId));
        System.out.println("Content size: " + options.valueOf(fContentSize));
        System.out.println("Document Type: " + options.valueOf(fDocType));
        System.out.println("Folder id used as root: " + options.valueOf(fRootFolder));
        System.out.println("Delete all objects after creation: " + options.valueOf(fCleanup));
        System.out.println("Number of actions to perform: " + options.valueOf(fCount));
        System.out.println("Number of threads to start: " + options.valueOf(fThreads));
        System.out.println("Kind of created content: " + options.valueOf(fContentKindStr));
    }

    private void createSingleDocument(OptionSet options) {
        System.out.println();
        System.out.println("Creating document with parameters:");
        printParameters(options);
        int noThreads = options.valueOf(fThreads);
        if (noThreads <= 1) {
            createSingleDocument(options.valueOf(fRepoId), options.valueOf(fDocType), options.valueOf(fContentSize),
                    options.valueOf(fRootFolder), options.valueOf(fCount), options.valueOf(fCleanup));
        } else {
            createSingleDocumentMT(noThreads, options.valueOf(fRepoId), options.valueOf(fDocType), options
                    .valueOf(fContentSize), options.valueOf(fRootFolder), options.valueOf(fCount), options
                    .valueOf(fCleanup));
        }
    }

    private void fillRepository(OptionSet options) {
        System.out.println();
        printParameters(options);
        System.out.println("Creating object tree with folowing parameters: ");
        System.out.println("Documents per folder: " + options.valueOf(fDocsPerFolder));
        System.out.println("Folder per folder: " + options.valueOf(fFolderPerFolder));
        System.out.println("Depth: " + options.valueOf(fDepth));
        System.out.println("Folder Type: " + options.valueOf(fFolderType));

        int noThreads = options.valueOf(fThreads);
        if (noThreads <= 1) {
            fillRepository(options.valueOf(fRepoId), options.valueOf(fDocsPerFolder),
                    options.valueOf(fFolderPerFolder), options.valueOf(fDepth), options.valueOf(fDocType), options
                            .valueOf(fFolderType), options.valueOf(fContentSize), options.valueOf(fRootFolder), options
                            .valueOf(fCleanup));
        } else {
            fillRepositoryMT(noThreads, options.valueOf(fRepoId), options.valueOf(fDocsPerFolder), options
                    .valueOf(fFolderPerFolder), options.valueOf(fDepth), options.valueOf(fDocType), options
                    .valueOf(fFolderType), options.valueOf(fContentSize), options.valueOf(fRootFolder), options
                    .valueOf(fCleanup));
        }

    }

    private void createFolders(OptionSet options) {
        System.out.println();
        System.out.println("Creating folder with parameters:");
        printParameters(options);
        System.out.println("Folder Type: " + options.valueOf(fFolderType));
        int noThreads = options.valueOf(fThreads);
        if (noThreads <= 1) {
            createFolders(options.valueOf(fRepoId), options.valueOf(fFolderType), options.valueOf(fRootFolder), options
                    .valueOf(fCount), options.valueOf(fCleanup));
        } else {
            createFoldersMT(noThreads, options.valueOf(fRepoId), options.valueOf(fFolderType), options
                    .valueOf(fRootFolder), options.valueOf(fCount), options.valueOf(fCleanup));
        }
    }

    private void createSingleDocument(String repoId, String documentType, int contentSizeInKB, String rootFolderId,
            int docCount, boolean doCleanup) {

        MultiThreadedObjectGenerator.ObjectGeneratorRunner runner = MultiThreadedObjectGenerator
                .prepareForCreateDocument(getBinding(), repoId, documentType, contentSizeInKB, rootFolderId, docCount,
                        fContentKind, doCleanup);
        ObjectGenerator gen = runner.getObjectGenerator();
        String[] ids = runner.doCreateDocument();
        System.out.println();
        System.out.println("Result:");
        System.out.println("Document creation succeeded.");
        System.out.println("Folder used as root for creation: " + rootFolderId);
        System.out.println("Ids of created documents: ");
        if (null == ids) {
            System.out.println("<none>");
        } else {
            for (int i = 0; i < ids.length; i++) {
                System.out.println(ids[i]);
            }
        }
        gen.printTimings();
        gen.resetCounters();
    }

    private void createSingleDocumentMT(int noThreads, String repoId, String documentType, int contentSizeInKB,
            String rootFolderId, int docCount, boolean doCleanup) {

        MultiThreadedObjectGenerator.ObjectGeneratorRunner[] runners = MultiThreadedObjectGenerator
                .prepareForCreateDocumentMT(noThreads, getBinding(), repoId, documentType, contentSizeInKB,
                        rootFolderId, docCount, fContentKind, doCleanup);

        MultiThreadedObjectGenerator.runMultiThreaded(runners);
        System.out.println("Document creation succeeded. All threads terminated.");
    }

    private void createFolders(String repoId, String folderType, String rootFolderId, int noFolders, boolean doCleanup) {

        MultiThreadedObjectGenerator.ObjectGeneratorRunner runner = MultiThreadedObjectGenerator
                .prepareForCreateFolder(getBinding(), repoId, folderType, rootFolderId, noFolders, doCleanup);
        ObjectGenerator gen = runner.getObjectGenerator();
        String[] ids = runner.doCreateFolder();
        System.out.println();
        System.out.println("Result:");
        System.out.println("Folder creation succeeded.");
        System.out.println("Ids of created folders: ");
        if (null == ids) {
            System.out.println("<none>");
        } else {
            for (int i = 0; i < ids.length; i++) {
                System.out.println(ids[i]);
            }
        }
        gen.printTimings();
        gen.resetCounters();
    }

    private void createFoldersMT(int noThreads, String repoId, String folderType, String rootFolderId, int noFolders,
            boolean doCleanup) {

        MultiThreadedObjectGenerator.ObjectGeneratorRunner[] runners = MultiThreadedObjectGenerator
                .prepareForCreateFolderMT(noThreads, getBinding(), repoId, folderType, rootFolderId, noFolders,
                        doCleanup);
        MultiThreadedObjectGenerator.runMultiThreaded(runners);
        System.out.println("Folder creation succeeded.");
    }

    private void callRepoInfo(String repositoryId, int count) {
        RepositoryService repSvc = getBinding().getRepositoryService();
        TimeLogger timeLogger = new TimeLogger("RepoInfoTest");
        RepositoryInfo repoInfo = null;
        for (int i = 0; i < count; i++) {
            binding.clearRepositoryCache(repositoryId);
            timeLogger.start();
            repoInfo = repSvc.getRepositoryInfo(repositoryId, null);
            timeLogger.stop();
        }
        System.out.println("Root Folder id is: " + (repoInfo == null ? "<unknown>" : repoInfo.getRootFolderId()));
        timeLogger.printTimes();
    }

    private void createTypes(OptionSet options) {

        String repoId = options.valueOf(fRepoId);
        String fileName = options.valueOf(fFileName);
        System.out.println();
        System.out.println("Not yet implemented waiting for CMIS 1.1!");
//        System.out.println("Creating types from file:");
//        System.out.println("File Name: " + fileName);
//        System.out.println("Repository Id: " + repoId);
//
//        File file = new File(options.valueOf(fFileName));
//        TypeDefinitionList typeDefs = null;
//
//        try {
//            Unmarshaller u = JaxBHelper.createUnmarshaller();
//            JAXBElement<CmisTypeDefinitionListType> type = (JAXBElement<CmisTypeDefinitionListType>) u.unmarshal(file);
//            typeDefs = Converter.convert(type.getValue());
//        } catch (Exception e) {
//            System.out.println("Could not load type: '" + fFileName + "': " + e);
//        }
//        MultiThreadedObjectGenerator.ObjectGeneratorRunner runner = MultiThreadedObjectGenerator.prepareForCreateTypes(
//                getBinding(), repoId, typeDefs);
//        ObjectGenerator gen = runner.getObjectGenerator();
//        gen.createTypes(typeDefs);
    }
        
    private void repositoryInfo(OptionSet options) {
        callRepoInfo(options.valueOf(fRepoId), options.valueOf(fCount));
    }

    private void createFiles(OptionSet options) {
        ContentStream contentStream = null;
        String fileNamePattern = options.valueOf(fFileNamePattern);
        int count = options.valueOf(fCount);
        int contentSize = options.valueOf(fContentSize);
        
        System.out.println("Creating local files with content: ");
        System.out.println("Kind: " + options.valueOf(fDocsPerFolder));
        System.out.println("Number of files: " + count);
        System.out.println("File name pattern: " + fileNamePattern);
        System.out.println("Kind of content: " + options.valueOf(fContentKindStr));
        System.out.println("Size of content (text only): " + contentSize);
        
        ObjectGenerator objGen = new ObjectGenerator(null, null, null, null, null, fContentKind);
        objGen.setContentSizeInKB(contentSize);
        
        InputStream is = null;
        FileOutputStream os = null;
        
        try {
            for (int i=0; i<count; i++) {
                String fileName = String.format(fileNamePattern, i);
                System.out.println("Generating file: " + fileName);
                if (contentSize > 0) {
                    switch (fContentKind) {
                    case StaticText:
                        contentStream = objGen.createContentStaticText();
                        break;
                    case LoremIpsumText:
                        contentStream =  objGen.createContentLoremIpsumText();
                        break;
                    case LoremIpsumHtml:
                        contentStream =  objGen.createContentLoremIpsumHtml();
                        break;
                    case ImageFractalJpeg:
                        contentStream =  objGen.createContentFractalimageJpeg();
                        break;
                    }
                }

                // write to a file:
                is = contentStream.getStream();
                os = new FileOutputStream (fileName);
                byte[] b = new byte[64 * 1024];  
                int read;  
                while ((read = is.read(b)) != -1)   
                    os.write(b, 0, read);  
                is.close();
                is = null;
                os.close();
                os = null;
            }
        } catch (Exception e) {
            System.err.println("Error generating file: " + e);
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                        is.close();
                if (null != os)
                    os.close();
            } catch (IOException e) {
            }            
        }
    }

    private CmisBinding getBinding() {
        if (binding == null) {
            if (fUsingAtom) {
                binding = createAtomBinding(getAtomPubUrl(), DEFAULT_USER, DEFAULT_PWD);
            } else {
                binding = createWsBinding(getWsUrl(), DEFAULT_USER, DEFAULT_PWD);
            }
        }
        return binding;
    }

    private static void filLoginParams(Map<String, String> parameters, String user, String password) {
        if (user != null && user.length() > 0) {
            parameters.put(SessionParameter.USER, user);
        }
        if (user != null && user.length() > 0) {
            parameters.put(SessionParameter.PASSWORD, password);
        }
    }

    private static CmisBinding createAtomBinding(String url, String user, String password) {

        // gather parameters
        Map<String, String> parameters = new HashMap<String, String>();
        filLoginParams(parameters, user, password);

        // get factory and create binding
        CmisBindingFactory factory = CmisBindingFactory.newInstance();
        parameters.put(SessionParameter.ATOMPUB_URL, url);
        CmisBinding binding = factory.createCmisAtomPubBinding(parameters);
        return binding;
    }

    private static CmisBinding createWsBinding(String url, String username, String password) {
        boolean isPrefix = true;
        String urlLower = url.toLowerCase();

        if (urlLower.endsWith("?wsdl")) {
            isPrefix = false;
        } else if (urlLower.endsWith(".wsdl")) {
            isPrefix = false;
        } else if (urlLower.endsWith(".xml")) {
            isPrefix = false;
        }

        return createBinding(url, isPrefix, username, password);
    }

    public static CmisBinding createBinding(String url, boolean isPrefix, String username, String password) {
        // gather parameters
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(SessionParameter.USER, username);
        parameters.put(SessionParameter.PASSWORD, password);

        if (!isPrefix) {
            parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, url);
        } else {
            parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, url + "RepositoryService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, url + "NavigationService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, url + "ObjectService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, url + "VersioningService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, url + "DiscoveryService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, url + "RelationshipService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, url + "MultiFilingService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, url + "PolicyService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, url + "ACLService?wsdl");
        }

        // get factory and create provider
        CmisBindingFactory factory = CmisBindingFactory.newInstance();
        CmisBinding binding = factory.createCmisWebServicesBinding(parameters);

        return binding;
    }

    private static String getAtomPubUrl() {
        return System.getProperty(PROP_ATOMPUB_URL, DEFAULT_ATOMPUB_URL);
    }

    private static String getWsUrl() {
        return System.getProperty(PROP_WS_URL, DEFAULT_WS_URL);
    }

    private String getConfiguredUrl() {
        return System.getProperty(PROP_URL, fUrlStr);
    }

    private static void getUrl(String urlStr) {
        URL url;
        InputStream is;
        InputStreamReader isr;
        BufferedReader r;
        String str;

        try {
            System.out.println("Reading URL: " + urlStr);
            url = new URL(urlStr);
            is = url.openStream();
            isr = new InputStreamReader(is);
            r = new BufferedReader(isr);
            do {
                str = r.readLine();
                if (str != null) {
                    System.out.println(str);
                }
            } while (str != null);
        } catch (MalformedURLException e) {
            System.out.println("Must enter a valid URL" + e);
        } catch (IOException e) {
            System.out.println("Can not connect" + e);
        }
    }

}
