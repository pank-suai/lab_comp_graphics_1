package lab2_import;

import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;


public class App {
    /**
     * Аналог функции gluPerspective в java
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

        // --- ЗАГРУЗКА МОДЕЛИ ---
        String modelPath = "models/banana.obj";
        // Флаг aiProcess_JoinIdenticalVertices для оптимизации
        int flags = aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_JoinIdenticalVertices;
        AIScene scene = aiImportFile(modelPath, flags);

        if (scene == null) {
            throw new RuntimeException("Не удалось загрузить модель! Ошибка: " + aiGetErrorString());
        }

        // --- ИНИЦИАЛИЗАЦИЯ ОКНА ---
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

        // --- НАСТРОЙКА КАМЕРЫ И ОСВЕЩЕНИЯ ---
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45.0f, width / (float) height, 0.1f, 100f);
        glMatrixMode(GL_MODELVIEW);

        // Добавляем простое освещение
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL); // Позволяет использовать glColor для материалов

        // --- ОБРАБОТКА ДАННЫХ МОДЕЛИ ---
        long meshPtr = Objects.requireNonNull(scene.mMeshes()).get(0);
        AIMesh mesh = AIMesh.create(meshPtr);

        // 1. Извлекаем ВЕРШИНЫ
        AIVector3D.Buffer aiVertices = mesh.mVertices();
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(aiVertices.remaining() * 3);
        while(aiVertices.remaining() > 0){
            AIVector3D v = aiVertices.get();
            verticesBuffer.put(v.x()).put(v.y()).put(v.z());
        }
        verticesBuffer.flip();

        // 2. Извлекаем НОРМАЛИ
        AIVector3D.Buffer aiNormals = Objects.requireNonNull(mesh.mNormals());
        FloatBuffer normalsBuffer = MemoryUtil.memAllocFloat(aiNormals.remaining() * 3);
        while(aiNormals.remaining() > 0){
            AIVector3D n = aiNormals.get();
            normalsBuffer.put(n.x()).put(n.y()).put(n.z());
        }
        normalsBuffer.flip();

        // 3. Извлекаем ИНДЕКСЫ
        int faceCount = mesh.mNumFaces();
        int indexCount = faceCount * 3; // Каждая грань - треугольник
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indexCount);
        AIFace.Buffer facesBuffer = Objects.requireNonNull(mesh.mFaces());
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("Face is not a triangle");
            }
            indicesBuffer.put(face.mIndices());
        }
        indicesBuffer.flip();

        // --- ЗАГРУЗКА ДАННЫХ НА GPU ---
        // Создаем VAO (Vertex Array Object) - он будет хранить все наши настройки
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Создаем VBO (Vertex Buffer Object) для вершин
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexPointer(3, GL_FLOAT, 0, 0); // Указываем, как читать данные вершин
        MemoryUtil.memFree(verticesBuffer); // Освобождаем память CPU

        // Создаем VBO для нормалей
        int nboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, nboId);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glNormalPointer(GL_FLOAT, 0, 0); // Указываем, как читать данные нормалей
        MemoryUtil.memFree(normalsBuffer);

        // Создаем EBO (Element Buffer Object) для индексов
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indicesBuffer);

        // Включаем массивы вершин и нормалей
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);

        // Отвязываем VAO, чтобы случайно не изменить его
        glBindVertexArray(0);

        // --- ГЛАВНЫЙ ЦИКЛ РЕНДЕРИНГА ---
        while (!glfwWindowShouldClose(window)) {
            glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glLoadIdentity();
            // Перемещаем камеру/сцену
            glTranslatef(0f, -1.0f, -5f);
            // Вращаем модель
            glRotatef((float) glfwGetTime() * 20f, 0f, 1f, 0f);

            // Задаем цвет модели
            glColor3f(1.0f, 0.8f, 0.3f);

            // --- КОМАНДА ОТРИСОВКИ ---
            glBindVertexArray(vaoId); // Выбираем нашу модель
            glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0); // Рисуем по индексам
            glBindVertexArray(0); // Отвязываем

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // --- ОЧИСТКА РЕСУРСОВ ---
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(nboId);
        glDeleteBuffers(eboId);

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}