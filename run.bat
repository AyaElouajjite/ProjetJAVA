@echo off
chcp 65001 >nul
echo ========================================
echo   Gestion de Reparations - Application
echo ========================================
echo.

REM Vérifier si Maven est installé
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Maven n'est pas installé ou n'est pas dans le PATH
    echo Veuillez installer Maven ou ajouter Maven au PATH
    echo.
    echo Téléchargement: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo [1/3] Compilation du projet avec Maven...
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] La compilation a échoué
    echo Essayez de voir les détails avec: mvn clean compile
    pause
    exit /b 1
)
echo [OK] Compilation réussie

echo.
echo [2/3] Copie des ressources...
call mvn resources:resources -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] La copie des ressources a échoué
    pause
    exit /b 1
)
echo [OK] Ressources copiées

echo.
echo [3/3] Lancement de l'application...
echo.
call mvn exec:java -Dexec.mainClass="presentation.MainApp" -Dexec.classpathScope=compile

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] Le lancement de l'application a échoué
    echo.
    echo Alternative: Essayez de compiler et lancer manuellement:
    echo   mvn clean package
    echo   java -cp "target/classes;target/dependency/*" presentation.MainApp
    pause
    exit /b 1
)

pause
