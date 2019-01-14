package unknowndomain.engine.client.rendering.model.assimp;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import unknowndomain.engine.client.rendering.util.GLDataType;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferElements;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

public class AssimpModel {
    private AIScene scene;
    private List<AssimpMesh> meshes;

    public AssimpModel(AIScene scene){
        this.scene = scene;
        int meshCount = scene.mNumMeshes();
        PointerBuffer meshesBuffer = scene.mMeshes();
        meshes = new ArrayList<>();
        for (int i = 0; i < meshCount; ++i) {
            meshes.add(new AssimpMesh(AIMesh.create(meshesBuffer.get(i))));
        }
    }

    public void free() {
        aiReleaseImport(scene);
        scene = null;
        meshes = null;
        //materials = null;
    }

    public void render(){
        //GLBufferFormats.POSITION_TEXTURE_NORMAL.bind();
        for (AssimpMesh mesh : meshes) {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getVertexBufferId());
            GL30.glVertexAttribPointer(0,3, GLDataType.FLOAT.glId, false,0,0);
            GL30.glEnableVertexAttribArray(0);
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getTexCoordBufferId());
            GL30.glVertexAttribPointer(2,2, GLDataType.FLOAT.glId, false,0,0);
            GL30.glEnableVertexAttribArray(2);
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getNormalBufferId());
            GL30.glVertexAttribPointer(3,3, GLDataType.FLOAT.glId, false,0,0);
            GL30.glEnableVertexAttribArray(3);


            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, mesh.getElementArrayBufferId());
            GL30.glDrawElements(GL11.GL_TRIANGLES,mesh.getElementCount(),GLDataType.UNSIGNED_INT.glId,0);
        }
    }
}