package optm;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class BlockGraphTest {

    @Test
    public void testDfsBlockGraph(){
        DfsBlockGraph dfsBlockGraph=new DfsBlockGraph();
        dfsBlockGraph.dfsParentToSons=new HashMap<>();
        dfsBlockGraph.dfsParentToSons.put(0,new HashSet<>(Collections.singletonList(1)));
        dfsBlockGraph.dfsParentToSons.put(1,new HashSet<>(Arrays.asList(2,4,8)));
        dfsBlockGraph.dfsParentToSons.put(2,new HashSet<>(Collections.singletonList(3)));
        dfsBlockGraph.dfsParentToSons.put(3,new HashSet<>());
        dfsBlockGraph.dfsParentToSons.put(4,new HashSet<>(Arrays.asList(5,7)));
        dfsBlockGraph.dfsParentToSons.put(5,new HashSet<>(Collections.singletonList(6)));
        dfsBlockGraph.dfsParentToSons.put(6,new HashSet<>());
        dfsBlockGraph.dfsParentToSons.put(7,new HashSet<>());
        dfsBlockGraph.dfsParentToSons.put(8,new HashSet<>());

        dfsBlockGraph.dfsSonToParent=new HashMap<>();
        dfsBlockGraph.dfsSonToParent.put(1,0);
        dfsBlockGraph.dfsSonToParent.put(2,1);
        dfsBlockGraph.dfsSonToParent.put(3,2);
        dfsBlockGraph.dfsSonToParent.put(4,1);
        dfsBlockGraph.dfsSonToParent.put(5,4);
        dfsBlockGraph.dfsSonToParent.put(7,4);
        dfsBlockGraph.dfsSonToParent.put(6,5);
        dfsBlockGraph.dfsSonToParent.put(8,1);

        dfsBlockGraph.flowsParentSons=new HashMap<>();
        dfsBlockGraph.flowsParentSons.put(0,new HashSet<>(Arrays.asList(1,2)));
        dfsBlockGraph.flowsParentSons.put(1,new HashSet<>(Arrays.asList(2,4,8)));
        dfsBlockGraph.flowsParentSons.put(2,new HashSet<>(Collections.singletonList(3)));
        dfsBlockGraph.flowsParentSons.put(3,new HashSet<>(Collections.singletonList(1)));
        dfsBlockGraph.flowsParentSons.put(4,new HashSet<>(Arrays.asList(5,7)));
        dfsBlockGraph.flowsParentSons.put(5,new HashSet<>(Arrays.asList(2,6)));
        dfsBlockGraph.flowsParentSons.put(6,new HashSet<>(Arrays.asList(0,3,4)));
        dfsBlockGraph.flowsParentSons.put(7,new HashSet<>(Collections.singletonList(6)));
        dfsBlockGraph.flowsParentSons.put(8,new HashSet<>(Arrays.asList(4,7)));

        dfsBlockGraph.flowsSonParents=new HashMap<>();
        dfsBlockGraph.flowsSonParents.put(0,new HashSet<>(Collections.singletonList(6)));
        dfsBlockGraph.flowsSonParents.put(1,new HashSet<>(Arrays.asList(0,3)));
        dfsBlockGraph.flowsSonParents.put(2,new HashSet<>(Arrays.asList(0,1,5)));
        dfsBlockGraph.flowsSonParents.put(3,new HashSet<>(Arrays.asList(2,6)));
        dfsBlockGraph.flowsSonParents.put(4,new HashSet<>(Arrays.asList(1,6,8)));
        dfsBlockGraph.flowsSonParents.put(5,new HashSet<>(Collections.singletonList(4)));
        dfsBlockGraph.flowsSonParents.put(6,new HashSet<>(Arrays.asList(5,7)));
        dfsBlockGraph.flowsSonParents.put(7,new HashSet<>(Arrays.asList(4,8)));
        dfsBlockGraph.flowsSonParents.put(8,new HashSet<>(Collections.singletonList(1)));

        dfsBlockGraph.vertexByMarks=new int[]{0,1,2,3,4,5,6,7,8};
        dfsBlockGraph.marksByVertex=new int[]{1,2,3,4,5,6,7,8,9};
        dfsBlockGraph.sdoms();
        assertArrayEquals(dfsBlockGraph.semiDominators,new int[]{0,1,1,2,2,5,2,2,2});
        dfsBlockGraph.rdoms();
        assertArrayEquals(dfsBlockGraph.relativeDominators,new int[]{0,1,1,2,4,5,4,4,8});
        dfsBlockGraph.idoms();
        assertArrayEquals(dfsBlockGraph.immediateDominators,new int[]{0,1,1,1,2,5,2,2,2});

    }

    private static void assertArrayEquals(int[] expected, int[] actual )
    {
        assertEquals(expected.length,actual.length);
        for(int i = 0; i < expected.length; i++)
        {
            assertEquals( expected[i], actual[i]);
        }
    }


}
