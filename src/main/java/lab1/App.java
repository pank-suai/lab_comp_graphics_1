package lab1;

import org.lwjgl.opengl.GL;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

class App{
    public static void main(String[] args){
        long window;
        if (!glfwInit()){
            throw new IllegalStateException("Не удаётся запустить GLFW");
        }
        window = glfwCreateWindow(640, 480, "Панков", NULL, NULL);
        if (window == NULL){
            glfwTerminate();
            throw new RuntimeException("Окно не создалось");
        }
        glfwSetWindowPos(window, 50, 50);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Render loop
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            glBegin(GL_POLYGON);
            glColor3f(1, 0, 0);
            glVertex3f(-0.6f, -0.75f, 0.5f);
            glColor3f(0, 1, 0);
            glVertex3f(0.6f, -0.75f, 0.5f);
            glColor3f(0, 0, 1);
            glVertex3f(0f, 0.75f, 0f);
            glEnd();


            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // Завершение работы приложения
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}