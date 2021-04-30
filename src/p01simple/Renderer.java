package p01simple;
//package lvl2advanced.p01gui.p01simple;

import lwjglutils.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.*;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    private int shaderProgramMain, shaderProgramPost;
    private OGLBuffers buffersMain;
    private int viewLocation, projectionLocation, typeLocation,timeLocation,modelLocation;
    private Camera camera;
    private Mat4PerspRH projection;
    private Mat4OrthoRH orthoRH;
    private Mat4 model;

    private OGLTexture2D textureMosaic;
    private OGLBuffers buffersPost;
    private boolean mousePressed, line,orthoView = false;
    private boolean projectionView = true;
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
        timeLocation=glGetUniformLocation(shaderProgramMain,"time");
        modelLocation=glGetUniformLocation(shaderProgramMain,"model");

        shaderProgramPost = ShaderUtils.loadProgram("/post");

        camera = new Camera()
                .withPosition(new Vec3D(5, 5, 2))//oddálení, přiblizeni
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        model=new Mat4RotY(0.0001);
        projection = new Mat4PerspRH(
                Math.PI / 3,
                height / (float) width,
                0.1,
                20
        );
        orthoRH = new Mat4OrthoRH(
                -20 * width / (float) height,
                20 * width / (float) height,
                0.1,
                20);
//        (-20 * width / (float) height, 20 * width / (float) height, -20, 20, 0.1f, 100.0f)


        buffersMain = TriangleFactory.generateTriangle(200, 200);
        buffersPost = TriangleFactory.generateTriangle(200, 200);
        renderTarget = new OGLRenderTarget(1920, 1680);

        try {
           textureMosaic = new OGLTexture2D("./mosaic.jpg");
           //textureMosaic = new OGLTexture2D("./hour.png");
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

        if (line) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        renderMain();
        renderPostProcessing();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glDisable(GL_DEPTH_TEST);
        viewer.view(textureMosaic, -1, -1, 0.5);
        viewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5);
        viewer.view(renderTarget.getDepthTexture(), -1, 0, 0.5);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
        textRenderer.addStr2D(2, 20, " Návod pro použítí");
    }

    private void renderMain() {
        glUseProgram(shaderProgramMain);
        renderTarget.bind(); // render to texture

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(viewLocation, false, camera.getViewMatrix().floatArray());

        glUniformMatrix4fv(modelLocation, false, model.floatArray());

        if (projectionView) {
            glUniformMatrix4fv(projectionLocation, false, projection.floatArray());
        }//projection
        else if (orthoView) {
            glUniformMatrix4fv(projectionLocation, false, orthoRH.floatArray());

        }

        textureMosaic.bind(shaderProgramMain, "textureMosaic", 0);

        /*
        objekty
         */
        glUniform1f(typeLocation, 0f);//dalsi objekt
        buffersMain.draw(GL_TRIANGLE_STRIP, shaderProgramMain);
        glUniform1f(typeLocation, 1.5f);
        buffersMain.draw(GL_TRIANGLE_STRIP, shaderProgramMain);
    }
    /*
    osvetleni
     */
    // nastaveni svetla
    // Prepare light parameters.
    float SHINE_ALL_DIRECTIONS = 1;
    float[] lightPos = { -30, 0, 0, SHINE_ALL_DIRECTIONS };
    float[] lightColorAmbient = { 0.2f, 0.2f, 0.2f, 1f };
    float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };

    private void renderPostProcessing() {
        glUseProgram(shaderProgramPost);
        glBindFramebuffer(GL_FRAMEBUFFER, 0); // render to window
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height); // must reset back - render target is setting its own viewport
        renderTarget.getColorTexture().bind(shaderProgramPost, "textureRendered", 0);
        buffersPost.draw(GL_TRIANGLE_STRIP, shaderProgramPost);
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
            if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                double[] il = new double[1];
                double[] ik = new double[1];
                glfwGetCursorPos(window, il, ik);

                model.mul(il[0]+oldMy);
                mousePressed = action == GLFW_PRESS;
                System.out.println("mouse right dodelat na rotaci"+il[0]);
            }
        }
    };
    /*
    metoda pro priblizeni koleckem mysi
     */
    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            camera = camera.forward(xoffset);
            camera = camera.backward(yoffset);
        }
    };
    /*
    metoda pro zpracovani vstupu z klavesnice
     */
    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            double speed = 0.25;
            switch (key) {
                case GLFW_KEY_W:
                    camera = camera.down(speed);
                    break;
                case GLFW_KEY_S:
                    camera = camera.up(speed);
                    break;
                case GLFW_KEY_A:
                    camera = camera.right(speed);
                    break;
                case GLFW_KEY_D:
                    camera = camera.left(speed);
                    break;
                case GLFW_KEY_KP_ADD:
                    camera = camera.forward(speed);
                    break;
                case GLFW_KEY_KP_SUBTRACT:
                    camera = camera.backward(speed);
                    break;
                case GLFW_KEY_L:
                    if (!line) {
                        line = true;
                    }
                    break;
                case GLFW_KEY_F:
                    if (line) {
                        line = false;
                    }
                    break;
                case GLFW_KEY_O:
                    if (!orthoView) {
                        orthoView = true;
                        projectionView = false;
                    }
                    break;
                case GLFW_KEY_P:
                    if (!projectionView) {
                        projectionView = true;
                        orthoView = false;
                    }
                    break;
                default:

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

    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }


}
