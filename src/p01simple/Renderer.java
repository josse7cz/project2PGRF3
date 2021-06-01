package p01simple;

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
    private int viewLocation, projectionLocation, modelLocation, locTime, animace, typeLocation, lightColor, objectColorLoc, lightPosLoc, viewPosLoc,widthBlur,heightBlur;
    private Camera camera;
    private Mat4PerspRH projection;
    private Mat4OrthoRH orthoRH;
    private Mat4 model;
    private OGLTexture2D textureMosaic, textureWood;
    private OGLBuffers buffersPost;
    private boolean mousePressed, line, orthoView, triangleLine = false;
    private boolean projectionView = true;
    private double oldMx, oldMy;
    private float time = 1;


    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;
    private double a = 0;

    private boolean point;
    private float objType2 = 1, objType1 = 0;
    private OGLTexture2D textureEarth;


    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        glClearColor(0.1f, 0.1f, 0.1f, 1f);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        shaderProgramMain = ShaderUtils.loadProgram("/main");
        shaderProgramPost = ShaderUtils.loadProgram("/post");
        modelLocation = glGetUniformLocation(shaderProgramMain, "model");
        viewLocation = glGetUniformLocation(shaderProgramMain, "view");
        projectionLocation = glGetUniformLocation(shaderProgramMain, "projection");
        typeLocation = glGetUniformLocation(shaderProgramMain, "type");
        locTime = glGetUniformLocation(shaderProgramMain, "time");
        objectColorLoc = glGetUniformLocation(shaderProgramMain, "objectCol");
        lightColor = glGetUniformLocation(shaderProgramMain, "lightColor");
        lightPosLoc = glGetUniformLocation(shaderProgramMain, "lightPos");
        viewPosLoc = glGetUniformLocation(shaderProgramMain, "viewPos");
        widthBlur=glGetUniformLocation(shaderProgramMain,"widthBlur");
        heightBlur=glGetUniformLocation(shaderProgramMain,"heightBlur");



        camera = new Camera()
                .withPosition(new Vec3D(5, 5, 2))//oddálení, přiblizeni
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        model = new Mat4RotY(0.001);
        projection = new Mat4PerspRH(
                Math.PI / 3,
                height / (float) width,
                0.1,
                30
        );
        orthoRH = new Mat4OrthoRH(
                -20 * width / (float) height,
                20 * width / (float) height,
                0.1,
                20);


        if (triangleLine) {
            buffersMain = GridFactory.generateGrid(200, 200);
            buffersPost = GridFactory.generateGrid(200, 200);
        } else
            buffersMain = TriangleFactory.generateTriangle(200, 200);
        buffersPost = TriangleFactory.generateTriangle(200, 200);

        renderTarget = new OGLRenderTarget(1920, 1680);

        try {
            textureMosaic = new OGLTexture2D("./mosaic.jpg");
            textureWood = new OGLTexture2D("./woodTexture.jpg");
            textureEarth = new OGLTexture2D("./globe.jpg");


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
            point = false;
        }
        if (point) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
            line = false;
        }
        renderMain();
        renderPostProcessing();
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisable(GL_DEPTH_TEST);
        viewer.view(textureEarth, -1, -1, 0.5);//zobrazení textury vlevo dole
        viewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5);
        viewer.view(renderTarget.getDepthTexture(), -1, 0, 0.5);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
        textRenderer.addStr2D(2, 20, " Návod pro použítí");
    }

    private void renderMain() {
        glUseProgram(shaderProgramMain);
        renderTarget.bind(); // render to texture
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUniformMatrix4fv(modelLocation, false, model.floatArray());
        glUniformMatrix4fv(viewLocation, false, camera.getViewMatrix().floatArray());
        glUniform3f(objectColorLoc, 1.0f, 0.5f, 0.31f);
        glUniform3f(lightColor, 1.0f, 0.5f, 1.0f);
        glUniform3f(lightPosLoc, 1.0f, 0.5f, -5.0f);
        glUniform3f(viewPosLoc, (float) camera.getPosition().getX(), (float) camera.getPosition().getY(), (float) camera.getPosition().getZ());
        if (projectionView) {
            glUniformMatrix4fv(projectionLocation, false, projection.floatArray());
        }//projection
        else if (orthoView) {
            glUniformMatrix4fv(projectionLocation, false, orthoRH.floatArray());
        }
        glUniform1f(locTime, time);

        if (animace == 1) {
            if (a < 30) {
                time += 0.1;
                a = a + 0.1;

            } else if (a >= 30 && a < 60) {
                time -= 0.1;
                a = a + 0.1;

            } else a = 0;
        }

        if (objType1 == 0) {
            textureMosaic.bind(shaderProgramMain, "textureMosaic", 0);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        } else if (objType1 == 1) {
            textureWood.bind(shaderProgramMain, "textureWood", 0);
        } else if (objType1 == 2) {
            textureEarth.bind(shaderProgramMain, "textureEarth", 0);
        }
        if (objType2 == 0) {
            textureMosaic.bind(shaderProgramMain, "textureMosaic", 0);
        } else if (objType2 == 1) {
            textureWood.bind(shaderProgramMain, "textureWood", 0);
        } else if (objType2 == 2) {
            textureEarth.bind(shaderProgramMain, "textureEarth", 0);
        }

        /*
        objekty
         */

        if (triangleLine) {

            glUniform1f(typeLocation, objType1);
            glUniformMatrix4fv(modelLocation, false, new Mat4Transl(2, 0, 0).floatArray());
            buffersMain.draw(GL_TRIANGLES, shaderProgramMain);

            glUniform1f(typeLocation, objType2);
//        glUniformMatrix4fv(..., false, new Mat4Transl(camera.getPosition()).floatArray());
            glUniformMatrix4fv(modelLocation, false, new Mat4Transl(-2, 0, 0).floatArray());
            buffersMain.draw(GL_TRIANGLES, shaderProgramMain);
        } else

            glUniform1f(typeLocation, objType1);//dalsi objekt
        glUniformMatrix4fv(modelLocation, false, new Mat4Transl(2, 0, 0).floatArray());
        buffersMain.draw(GL_TRIANGLE_STRIP, shaderProgramMain);

        glUniform1f(typeLocation, objType2);
        glUniformMatrix4fv(modelLocation, false, new Mat4Transl(-1.5, -0.5, 0).floatArray());
        buffersMain.draw(GL_TRIANGLE_STRIP, shaderProgramMain);

    }

    private void renderPostProcessing() {
        glUseProgram(shaderProgramPost);
        glBindFramebuffer(GL_FRAMEBUFFER, 0); // render to window
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height); // must reset back - render target is setting its own viewport
        glUniform1f(width,renderTarget.getWidth());
        glUniform1f(height,renderTarget.getHeight());


        renderTarget.getColorTexture().bind(shaderProgramPost, "textureRendered", 0);
        renderTarget.getDepthTexture().bind(shaderProgramPost,"textureDepth",1);
//      renderTarget.getColorTexture().bind(shaderProgramPost, "textureWood", 1);
//      renderTarget.getColorTexture().bind(shaderProgramPost, "textureEarth", 2);


        if (triangleLine) {
            buffersPost.draw(GL_TRIANGLES, shaderProgramPost);
        } else
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

                model.mul(il[0] + oldMy);
                mousePressed = action == GLFW_PRESS;
                System.out.println("mouse right dodelat na rotaci" + il[0]);
            }
        }
    };
    /*
    mouse scroll
     */
    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            camera = camera.forward(xoffset);
            camera = camera.backward(yoffset);
        }
    };

    /*
     keyboard
    */
    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
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
                        } else line = false;
                        break;
                    case GLFW_KEY_P:
                        if (!point) {
                            point = true;
                            line = false;
                        } else point = false;
                        break;

                    case GLFW_KEY_O:
                        if (!orthoView) {
                            orthoView = true;
                            projectionView = false;
                        } else {
                            projectionView = true;
                            orthoView = false;
                        }
                        break;

                    case GLFW_KEY_M:
                        if (!triangleLine) {
                            triangleLine = true;
                        } else triangleLine = false;
                        break;
                    case GLFW_KEY_Q:
                        if (animace == 1) {
                            animace = 0;
                        } else
                            animace = 1;
                        break;
                    case GLFW_KEY_1:
                        if (objType1 == 1) {
                            objType1 = 0;
                        } else
                            objType1 = 1;
                        break;
                    case GLFW_KEY_2:
                        if (objType1 == 2) {
                            objType1 = 0;
                        } else
                            objType1 = 2;
                        break;
                    case GLFW_KEY_3:
                        if (objType1 == 3) {
                            objType1 = 0;
                        } else
                            objType1 = 3;
                        break;
                    case GLFW_KEY_4:
                        if (objType1 == 4) {
                            objType1 = 0;
                        } else
                            objType1 = 4;
                        break;
                    case GLFW_KEY_5:
                        if (objType1 == 5) {
                            objType1 = 0;
                        } else
                            objType1 = 5;
                        break;
                    case GLFW_KEY_6:
                        if (objType1 == 6) {
                            objType1 = 0;
                        } else
                            objType1 = 6;
                        break;

                    case GLFW_KEY_9:
                        if (objType2 == 8) {
                            objType2 = 0;

                        } else
                            objType2 = objType2 + 1;
                        break;
                        case GLFW_KEY_LEFT_SHIFT:
                        objType2 = 0;
                        objType1 = 0;
                        break;
                    default:
                }

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
