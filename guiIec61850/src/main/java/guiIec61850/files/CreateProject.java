/*
 * Copyright 2021 Philipp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guiIec61850.files;

import java.io.IOException;
import static java.lang.System.getProperty;

/**
 *
 * @author Philipp
 */
public class CreateProject {

    public static String templateProPath = getProperty("user.dir") + "\\files\\template\\PrjTemplate\\TwinCATProject.tsproj";
    public static String proPath = getProperty("user.dir") + "\\files\\new";
    public static String proName = "testName.tsp";

            public static String command = "powershell.exe " + "$targetDir = '" + proPath + "';\n"
            + "$targetName = '" + proName + "';\n"
            + "$template = '" + templateProPath + "';\n"
            + "$dte = new-object -com TcXaeShell.DTE.15.0;\n"
            + "$dte.SuppressUI = $true;\n"
            + "$dte.MainWindow.visible = $false;\n"
            + "if(test-path $targetDir -pathtype container)\n"
            + "{\n"
            + "     Remove-Item $targetDir -Recurse -Force;\n"
            + "};\n"
            + "New-Item $targetDir –type directory;\n"
            + "$sln = $dte.Solution;\n"
            + "$project = $sln.AddFromTemplate($template,$targetDir,$targetName);\n"
            + "$systemManager = $project.Object;\n"
            + "$project.Save();\n"
            + "$solutionPath = $targetDir + '/' + $targetName;\n"
            + "$sln.SaveAs($solutionPath);\n";
    
    static void createProject() throws IOException {
        PowerShellCommand Proj = new PowerShellCommand();
        Proj.writeOnPowerShell(command);
    }

    public static void main(String[] args) throws IOException {
        createProject();
    }
}
