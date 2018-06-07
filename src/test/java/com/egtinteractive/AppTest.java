package com.egtinteractive;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.isA;

import com.egtinteractive.app.RecoveryManager;

@PrepareForTest(RecoveryManager.class)
public class AppTest {
    @ObjectFactory
    public IObjectFactory getObjectFactory() {
	return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void test() throws Exception {

	PowerMockito.mockStatic(RecoveryManager.class);
	when(RecoveryManager.isFileProcessed(isA(String.class))).thenReturn(true);
	Assert.assertEquals(true, RecoveryManager.isFileProcessed("SDSDD"));
    }
}
