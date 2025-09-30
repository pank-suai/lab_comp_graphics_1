package lab2;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.system.MemoryUtil.NULL;

public class App {
    public static void main(String[] args) {
        long window;
        if (!glfwInit()) {
            throw new IllegalStateException("Не удаётся запустить GLFW");
        }
        window = glfwCreateWindow(640, 480, "Панков", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Окно не создалось");
        }
        glfwSetWindowPos(window, 50, 50);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
// Render loop
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);
            glClear(GL_DEPTH_BUFFER_BIT);

            glLoadIdentity();
            glRotatef((float) glfwGetTime() * -10f, 0f, 1f, 0f);


            glBegin(GL_TRIANGLES);
            // Перёд
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 1.0f, 0.0f);
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);

            // Правый
            glColor3f(1.0f,0.0f,0.0f);
            glVertex3f( 0.0f, 1.0f, 0.0f);
            glColor3f(0.0f,0.0f,1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glColor3f(0.0f,1.0f,0.0f);
            glVertex3f( 1.0f,-1.0f, -1.0f);

            // Задний
            glColor3f(1.0f,0.0f,0.0f);
            glVertex3f( 0.0f, 1.0f, 0.0f);
            glColor3f(0.0f,1.0f,0.0f);
            glVertex3f( 1.0f,-1.0f, -1.0f);
            glColor3f(0.0f,0.0f,1.0f);
            glVertex3f(-1.0f,-1.0f, -1.0f);

            // Левый
            glColor3f(1.0f,0.0f,0.0f);
            glVertex3f( 0.0f, 1.0f, 0.0f);
            glColor3f(0.0f,0.0f,1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glColor3f(0.0f,1.0f,0.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glEnd();


            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // Завершение работы приложения
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
