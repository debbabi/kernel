/**
 * This file is part of the CRISTAL-iSE kernel.
 * Copyright (c) 2001-2015 The CRISTAL Consortium. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *
 * http://www.fsf.org/licensing/licenses/lgpl.html
 */
package org.cristalise.kernel.test.persistency;

import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.UUID;

import org.cristalise.kernel.lookup.AgentPath;
import org.cristalise.kernel.lookup.DomainPath;
import org.cristalise.kernel.lookup.ItemPath;
import org.cristalise.kernel.lookup.RolePath;
import org.cristalise.kernel.process.resource.Resource;
import org.cristalise.kernel.test.process.MainTest;
import org.cristalise.kernel.utils.CastorXMLUtility;
import org.cristalise.kernel.utils.CristalMarshaller;
import org.cristalise.kernel.utils.FileStringUtility;
import org.cristalise.kernel.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

public class CastorXMLTest {

    String ior = "IOR:005858580000001549444C3A69646C746573742F746573743A312E3000585858"+
            "0000000100000000000000350001005800000006636F726261009B44000000214F52"+
            "424C696E6B3A3A636F7262613A33393734383A3A736B656C65746F6E202330";

    CristalMarshaller marshaller;

    @Before
    public void setup() throws Exception {
        Logger.addLogStream(System.out, 6);
        Properties props = FileStringUtility.loadConfigFile(MainTest.class.getResource("/server.conf").getPath());

        Resource resource = new Resource();

        marshaller = new CastorXMLUtility(resource, props, resource.getKernelResourceURL("mapFiles/"));
    }

    /**
     * Compares 2 XML string
     *
     * @param expected the reference XML
     * @param actual the xml under test
     * @return whether the two XMLs are identical or not
     */
    public static boolean compareXML(String expected, String actual)  {
        Diff diffIdentical = DiffBuilder.compare(expected).withTest(actual)
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
                .ignoreComments()
                .ignoreWhitespace()
                .checkForSimilar()
                .build();

        if(diffIdentical.hasDifferences()){
            Logger.msg(0, actual);
            Logger.warning(diffIdentical.toString());
        }

        return !diffIdentical.hasDifferences();
    }

    @Test
    public void testCastorItemPath() throws Exception {
        ItemPath item      = new ItemPath(UUID.randomUUID(), ior);
        ItemPath itemPrime = (ItemPath) marshaller.unmarshall(marshaller.marshall(item));

        assertEquals( item.getUUID(),      itemPrime.getUUID());
        assertEquals( item.getIORString(), itemPrime.getIORString());

        Logger.msg(marshaller.marshall(itemPrime));
    }

    @Test
    public void testCastorAgentPath() throws Exception {
        AgentPath agent      = new AgentPath(UUID.randomUUID(), ior, "toto");
        AgentPath agentPrime = (AgentPath) marshaller.unmarshall(marshaller.marshall(agent));

        assertEquals( agent.getUUID(),      agentPrime.getUUID());
        assertEquals( agent.getIORString(), agentPrime.getIORString());
        assertEquals( agent.getAgentName(), agentPrime.getAgentName());

        Logger.msg(marshaller.marshall(agentPrime));
    }

    @Test
    public void testCastorDomainPath_Context() throws Exception {
        DomainPath domain      = new DomainPath("/domain/path");
        DomainPath domainPrime = (DomainPath) marshaller.unmarshall(marshaller.marshall(domain));

        assertEquals( domain.getStringPath(), domainPrime.getStringPath());

        Logger.msg(marshaller.marshall(domainPrime));
    }

    @Test
    public void testCastorDomainPath_WithTarget() throws Exception {
        DomainPath domain      = new DomainPath("/domain/path", new ItemPath());
        DomainPath domainPrime = (DomainPath) marshaller.unmarshall(marshaller.marshall(domain));

        assertEquals( domain.getStringPath(), domainPrime.getStringPath());
        assertEquals( domain.getTargetUUID(), domainPrime.getTargetUUID());

        Logger.msg(marshaller.marshall(domainPrime));
    }

    @Test
    public void testCastorRolePath() throws Exception {
        RolePath role      = new RolePath("Minion", false);
        RolePath rolePrime = (RolePath) marshaller.unmarshall(marshaller.marshall(role));

        assertEquals( role.getStringPath(), rolePrime.getStringPath());
        assertEquals( role.hasJobList(), rolePrime.hasJobList());

        Logger.msg(marshaller.marshall(rolePrime));
    }
}
