package lab2;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.system.MemoryUtil.NULL;

public class App {

    /**
     * Аналог функции gluPerspective в java
     *
     * @param fovy поле зрения
     * @param aspect соотношение сторон
     * @param near видим объекты *от*
     * @param far видим объекты *до*
     */
    public static void gluPerspective(float fovy, float aspect, float near, float far) {

        float bottom = -near * (float) Math.tan(fovy / 2);

        float top = -bottom;

        float left = aspect * bottom;

        float right = -left;

        glFrustum(left, right, bottom, top, near, far);

    }


    public static void main(String[] args) {
        long window;
        final int width = 600;
        final int height = 600;
        if (!glfwInit()) {
            throw new IllegalStateException("Не удаётся запустить GLFW");
        }
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(width, height, "Панков", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Окно не создалось");
        }
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45.0f, width / (float) height, 0.1f, 100f);
        glMatrixMode(GL_MODELVIEW);
        // Render loop
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glLoadIdentity();
            glTranslatef(0f, 0.0f, -10f);
            glRotatef((float) glfwGetTime() * -10f, 0.5f, 1f, 0f);

            glBegin(GL_TRIANGLES);
            // Перёд
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 1.0f, 0.0f);
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);

            // Правый
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 1.0f, 0.0f);
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);

            // Задний
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 1.0f, 0.0f);
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);

            // Левый
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 1.0f, 0.0f);
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glEnd();


            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // Завершение работы приложения
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
