package org.app.core.options;

import glm_.vec2.Vec2i;
import org.apache.commons.io.FileUtils;
import org.app.utils.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EngineOptions {
    private String projectName;
    private String projectVersion;
    private Vec2i windowResolution;
    private float framerateCap;
    private String inputMappingPath;
    private String mainScenePath;

    public EngineOptions(File file) {
        loadConfiguration(file);
    }

    private void loadConfiguration(File file) {
        if (!file.isFile()) {
            Logger.logError(String.format(
                    "Tried to load file, that doesn't exist: '%s'",
                    file.getAbsolutePath()
            ));
            return;
        }
        if (!file.canRead()) {
            Logger.logError(String.format(
                    "Tried to read file '%s' without permission",
                    file.getAbsolutePath()
            ));
            return;
        }

        JSONObject jsonObject;
        try {
            String content = FileUtils.readFileToString(file);
            jsonObject = new JSONObject(content);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            this.projectName = jsonObject.getString("Project Name");
            this.projectVersion = jsonObject.getString("Project Version");
            JSONArray windowResolution1 = jsonObject.getJSONArray("Window Resolution");
            this.windowResolution = new Vec2i(
                    windowResolution1.getInt(0),
                    windowResolution1.getInt(1)
            );
            this.framerateCap = (float) jsonObject.getDouble("Framerate cap");
            this.inputMappingPath = jsonObject.getString("Input mapping path");
            this.mainScenePath = jsonObject.getString("Main scene path");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveConfiguration(File file) {
        if (!file.exists()) {
            if ( !file.delete() ) {
                Logger.logError(String.format(
                        "Tried to delete file '%s', but failed",
                        file.getAbsolutePath()
                ));
                return;
            }
        }

        try {
            if ( !file.createNewFile() ) {
                Logger.logError(String.format(
                        "Tried to create file '%s', but failed",
                        file.getAbsolutePath()
                ));
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if ( !file.canRead() ) {
            Logger.logError(String.format(
                    "Tried to read file '%s' without permission",
                    file.getAbsolutePath()
            ));
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("Project Name", this.projectName);
            object.put("Project Version", this.projectVersion);
            JSONArray windowResolution1 = new JSONArray();
            windowResolution1.put(windowResolution.getX());
            windowResolution1.put(windowResolution.getY());
            object.put("Window Resolution", windowResolution1);
            object.put("Framerate cap", this.projectName);
            object.put("Input mapping path", this.projectName);
            object.put("Main scene path", this.projectName);

            FileWriter writer = new FileWriter(file);
            writer.write(object.toString());
            writer.close();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public Vec2i getWindowResolution() {
        return windowResolution;
    }

    public void setWindowResolution(Vec2i windowResolution) {
        this.windowResolution = windowResolution;
    }

    public float getFramerateCap() {
        return framerateCap;
    }

    public void setFramerateCap(float framerateCap) {
        this.framerateCap = framerateCap;
    }

    public String getInputMappingPath() {
        return inputMappingPath;
    }

    public void setInputMappingPath(String inputMappingPath) {
        this.inputMappingPath = inputMappingPath;
    }

    public String getMainScenePath() {
        return mainScenePath;
    }

    public void setMainScenePath(String mainScenePath) {
        this.mainScenePath = mainScenePath;
    }
}
