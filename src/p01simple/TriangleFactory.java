package p01simple;

import lwjglutils.OGLBuffers;


public class TriangleFactory {

    static OGLBuffers generateTriangle(int m, int n) {


        float[] vb = new float[(m * n) * 2];
        int index = 0;


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                vb[index++] = j / (float) (m - 1);
                vb[index++] = i / (float) (n - 1);

            }
        }


        int[] ib = new int[2 * (m * (n))];//počet prvků index bufferu 2 *( m * n)
        int index2 = 0;

        for (int k = 0; k < (n - 1); k++) {

            if (index % 2 == 0) {//sudý průchod

                for (int x = 0; x < m; x++) {
                    ib[index2++] = ((k * n) + x);
                    ib[index2++] = (((k + 1) * n) + x);
                }
                index++;

                if (k < n - 2) {
                    // Degener. pravá
                    ib[index2++] = (((k + 1) * n) + (m - 1));
                    ib[index2++] = (((k + 1) * n) + (m - 1));
                }
            } else if (index % 2 != 0) {
                for (int x = 0; x < m; x++) {//lichý průchod
                    ib[index2++] = ((((k + 1) * n) - x) + (n - 1));
                    ib[index2++] = (((k * n) - x) + (n - 1));

                }
                index++;
                if (k < n - 2) {
                    // Degener. levá
                    ib[index2++] = (((k) * n) + (m));
                    ib[index2++] = (((k) * n) + (m));

                }

            }


        }


        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };
        return new OGLBuffers(vb, attributes, ib);

    }

}