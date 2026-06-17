package com.cl.mdd.server.contract.config;

import com.cl.mdd.server.contract.ContractsRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ContractConfig.class)
public class ContractConfigIT {

    @Autowired
    private ContractsRegistry contractsRegistry;

    @Test
    public void testRegistryReady() {
        Assert.assertNotNull(contractsRegistry.practiceContract());
        Assert.assertNotNull(contractsRegistry.professionalContract());
    }

}
