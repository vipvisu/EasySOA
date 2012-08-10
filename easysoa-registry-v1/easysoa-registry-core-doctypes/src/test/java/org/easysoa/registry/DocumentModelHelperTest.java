package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.RepositoryDoctype;
import org.easysoa.registry.types.TaggingFolderDoctype;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy("org.easysoa.registry.core.doctypes")
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class DocumentModelHelperTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SoaNodeRepositoryTest.class);

    @Inject
    CoreSession documentManager;
    
    @Inject
    DocumentService documentService;
    
    @Test
    public void testModelCreation() throws ClientException {
        DocumentModel systemModel = documentService.create(documentManager, TaggingFolderDoctype.DOCTYPE,
                DocumentModelHelper.WORKSPACEROOT_REF.toString(),
                "MySystem", "MySystemTitle");
        documentManager.save();
        Assert.assertNotNull(systemModel);
        Assert.assertEquals(systemModel.getName(), "MySystem");
        Assert.assertEquals(systemModel.getTitle(), "MySystemTitle");
    }

    @Test
    public void testModelQuery() throws ClientException {
        DocumentModel systemModel = documentService.find(documentManager, TaggingFolderDoctype.DOCTYPE, "MySystem");
        Assert.assertNotNull("Created system must be found by name", systemModel);
        Assert.assertEquals(systemModel.getTitle(), "MySystemTitle");
        Assert.assertTrue("Returned document must be in the repository, in the System folder",
                systemModel.getPathAsString().startsWith(RepositoryDoctype.REPOSITORY_REF.toString()));
    }
}
