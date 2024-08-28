; -- configTool.iss by HeikkiJ --
;
; This script generates setup.exe for NetworkIQ ConfigTool.
;
; "InnoSetup" and "InnoSetup Script Includes" must be installed
;    to compile this file. Best way to get these is to install
;    QuickStart Pack for Inno Setup
;
; Please check all defines
;    before compiling script.

#define CompanyName          "Ericsson"
#define Copyright            "Copyright © 2007, Ericsson."
#define HomePage             "http://www.ericsson.com/"
#define SupportPage          "http://www.ericsson.com/"
#define ApplicationName      "ConfigTool"
#define SetupExeName         "eniq_configtool_5-0-0_b387_R6_setup"
#define ConfigToolAppVersion "5.0"
#define ConfigToolAppBuild   "5.0.0"
#define ConfigToolAppName    "Network IQ Config Tool"

[Setup]
AppName={#ConfigToolAppName}
AppId={#ConfigToolAppName} {#ConfigToolAppVersion}
AppVerName={cm:NameAndVersion,{#ConfigToolAppName},{#ConfigToolAppBuild}}
AppVersion={#ConfigToolAppBuild}
AppPublisher={#CompanyName}
AppPublisherURL={#HomePage}
AppSupportURL={#SupportPage}
AppCopyright={#Copyright}
DefaultDirName={pf}\{#CompanyName}\{#ApplicationName}
PrivilegesRequired=admin
ShowLanguageDialog=no
LanguageDetectionMethod=none
OutputBaseFilename={#SetupExeName}
VersionInfoVersion={#ConfigToolAppBuild}
VersionInfoCompany={#CompanyName}
VersionInfoDescription={#ConfigToolAppName}
VersionInfoTextVersion={#ConfigToolAppBuild}
VersionInfoCopyright={#Copyright}
OutputDir=.
DefaultGroupName=ENIQ Config Tool

[Types]
Name: full; Description: Full {#ConfigToolAppName} Installation

[Files]
Source: ..\dclib\*; DestDir: {app}\dclib
Source: ..\lib\*; DestDir: {app}\lib
Source: 16 x 16 Blue_ICO.ico; DestDir: {app}
Source: 32 x 32 Blue_ICO.ico; DestDir: {app}

[_ISTool]
EnableISX=true

[Icons]
Name: {group}\Start Config Tool; Filename: javaw; Parameters: " -Xmx256m -jar ""{app}\dclib\configtool.jar"""; WorkingDir: {app}; IconFilename: {app}\16 x 16 Blue_ICO.ico; IconIndex: 0
Name: {userdesktop}\Start Config Tool; Filename: javaw; Parameters: " -Xmx256m -jar ""{app}\dclib\configtool.jar"""; WorkingDir: {app}; IconFilename: {app}\32 x 32 Blue_ICO.ico; IconIndex: 0
