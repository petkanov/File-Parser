package com.egtinteractive;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import com.egtinteractive.app.RecoveryManager;

@PrepareForTest(RecoveryManager.class)
public class ServiceTest {
    @ObjectFactory
    public IObjectFactory getObjectFactory() {
	return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void test() throws Exception {

	PowerMockito.mockStatic(RecoveryManager.class);
	when(RecoveryManager.isFileProcessed(isA(String.class))).thenReturn(true);
	boolean result = RecoveryManager.isFileProcessed("SDSDD");
//	Mockito.verify(RecoveryManager.isFileProcessed(isA(String.class)),Mockito.times(1));
	Assert.assertEquals(true,result);
    }
}
