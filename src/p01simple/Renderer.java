package p01simple;
//package lvl2advanced.p01gui.p01simple;

import lwjglutils.*;
import org.lwjgl.glfw.*;
import transforms.Camera;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    private int shaderProgramMain, shaderProgramPost;
    private OGLBuffers buffersMain;
    private int viewLocation, projectionLocation, typeLocation;
    private Camera camera;
    private Mat4PerspRH projection;
    private OGLTexture2D textureMosaic;
    private OGLBuffers buffersPost;

    private boolean mousePressed = false;
    private double oldMx, oldMy;
    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;

    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        glClearColor(0.1f, 0.1f, 0.1f, 1f);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        shaderProgramMain = ShaderUtils.loadProgram("/main");
        viewLocation = glGetUniformLocation(shaderProgramMain, "view");
        projectionLocation = glGetUniformLocation(shaderProgramMain, "projection");
        typeLocation = glGetUniformLocation(shaderProgramMain, "type");

        shaderProgramPost = ShaderUtils.loadProgram("/post");

        camera = new Camera()
                .withPosition(new Vec3D(3, 3, 2))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        projection = new Mat4PerspRH(
                Math.PI / 3,
                height / (float) width,
                0.1,
                20
        );

//        float[] vertexBufferData = {
//                -1, -1,
//                1, 0,
//                0, 1,
//        };
//        int[] indexBufferData = {0, 1, 2};
//        OGLBuffers.Attrib[] attributes = {
//                new OGLBuffers.Attrib("inPosition", 2),
//        };
//        buffers = new OGLBuffers(vertexBufferData, attributes, indexBufferData);

        buffersMain = GridFactory.generateGrid(50, 50);
        buffersPost = GridFactory.generateGrid(2, 2);

        renderTarget = new OGLRenderTarget(1024, 1024);

        try {
            textureMosaic = new OGLTexture2D("./mosaic.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewer = new OGLTexture2D.Viewer();
        textRenderer = new OGLTextRenderer(width, height);
    }

    @Override
    public void display() {
        glEnable(GL_DEPTH_TEST);
        // text-renderer disables depth-test (z-buffer)

        renderMain();
        renderPostProcessing();

        glDisable(GL_DEPTH_TEST);
        viewer.view(textureMosaic, -1, -1, 0.5);
        viewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5);
        viewer.view(renderTarget.getDepthTexture(), -1, 0, 0.5);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
    }

    private void renderMain() {
        glUseProgram(shaderProgramMain);
        renderTarget.bind(); // render to texture

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(viewLocation, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(projectionLocation, false, projection.floatArray());

        textureMosaic.bind(shaderProgramMain, "textureMosaic", 0);

        glUniform1f(typeLocation, 0f);
        buffersMain.draw(GL_TRIANGLES, shaderProgramMain);
        glUniform1f(typeLocation, 1f);
        buffersMain.draw(GL_TRIANGLES, shaderProgramMain);
    }

    private void renderPostProcessing() {
        glUseProgram(shaderProgramPost);
        glBindFramebuffer(GL_FRAMEBUFFER, 0); // render to window
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height); // must reset back - render target is setting its own viewport

        renderTarget.getColorTexture().bind(shaderProgramPost, "textureRendered", 0);
        buffersPost.draw(GL_TRIANGLES, shaderProgramPost);
    }

    private final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mousePressed) {
                camera = camera.addAzimuth(Math.PI / 2 * (oldMx - x) / width);
                camera = camera.addZenith(Math.PI / 2 * (oldMy - y) / height);
                oldMx = x;
                oldMy = y;
            }
        }
    };

    private final GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                double[] xPos = new double[1];
                double[] yPos = new double[1];
                glfwGetCursorPos(window, xPos, yPos);
                oldMx = xPos[0];
                oldMy = yPos[0];
                mousePressed = action == GLFW_PRESS;
            }
        }
    };

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cursorPosCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mouseButtonCallback;
    }

}
