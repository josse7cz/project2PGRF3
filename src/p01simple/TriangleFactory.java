package p01simple;

import lwjglutils.OGLBuffers;


public class TriangleFactory {
    private static int offset = 0;

    static OGLBuffers generateTriangle(int m, int n) {


        float[] vb = new float[m * n * 2 * 3];
        int index = 0;


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                vb[index++] = j / (float) (m);
                vb[index++] = i / (float) (n);

            }
        }


        int[] ib = new int[2* m * (n)];//počet prvků index bufferu 2*m(n-1)
        int index2 = 0;

        /**
         * funkcni pro prvni hodnoty
         */
        for (int y = 0; y < n - 1; y++) {
            if (y > 0) {
                // Degenerate begin: repeat first vertex
                //heightMapIndexData[offset++]= ((y * n));
                ib[index2++] = ((y * n));
                System.out.println(((y * n)));
            }

            for (int x = 0; x < m; x++) {
                // One part of the strip
                ib[index2++] = (((y * n) + x));
                ib[index2++] = ((((y + 1) * n) + x));
                System.out.println((y * n) + x);
                System.out.println((((y + 1) * n) + x));
            }

            if (y < n - 2) {
                // Degenerate end: repeat last vertex
                ib[index2++] = ((((y + 1) * n) + (m - 1)));
                System.out.println((((y + 1) * n) + (m - 1)));
            }
        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };
        return new OGLBuffers(vb, attributes, ib);

    }

}