@ECHO OFF
ECHO.
SETLOCAL
PUSHD %~dp0%
IF "%1" == "" goto start
if "%1" == "auto" (
	ECHO Running all tests
	goto ALL
) ELSE (
ECHO	invalid argument
ECHO.
ECHO	Usage: %0 [auto] 
ECHO			to run all TechPack IDE tests
ECHO		 %0 
ECHO			to run specific tests interactively.
	goto end

)
:start
ECHO		==========================================
ECHO		This lets tester to run TechPack IDE tests 
ECHO		interactively		
ECHO		==========================================
ECHO.
ECHO.
ECHO		==========================================
ECHO		Please select test class you want to run.
ECHO		==========================================
ECHO.
ECHO		1.	ALL
ECHO		2.	Manage TechPack View
ECHO		3.	IDE General Login
ECHO		4.	Measurement Types
ECHO		5.	Busy Hour
ECHO		6.	Reference Types
ECHO		7.	Transformers
ECHO		8.	Sets/Actions/Schedulings
ECHO		9.	Data Formats
ECHO		10.	External Statement
ECHO		11.	Universe
ECHO		12.	Interfaces
ECHO		13.	Activate TechPack
ECHO		14.	De-Activate TechPack
ECHO		15.	Upgrade TechPack
ECHO		16.	BusyHour Activation 
ECHO		Q/q.	Quit
ECHO.
:invalid
set choice=1
set /p choice="Enter your choice : " 
if %choice%==1 goto ALL
if %choice%==2 goto TPView
if %choice%==3 goto General
if %choice%==4 goto Measurement
if %choice%==5 goto BH
if %choice%==6 goto Reference
if %choice%==7 goto Transformers
if %choice%==8 goto Sets
if %choice%==9 goto DF
if %choice%==10 goto ES
if %choice%==11 goto Universe
if %choice%==12 goto Interface
if %choice%==13 goto Activate
if %choice%==14 goto DeActivate
if %choice%==15 goto Upgrade
if %choice%==16 goto BHActivation
if /i %choice%==q goto quit


POPD
ENDLOCAL
ECHO.
ECHO	invalid choice: %choice%
ECHO.
goto start


:ALL
ECHO		==========================================
ECHO		Executing All Test Cases
ECHO		==========================================
ant -file unittest_build.xml -propertyfile ide_build.properties run 
ECHO		Finished executing ALL tests

:TPView
ECHO		==========================================
ECHO		Executing Manage TechPack View Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=ManageTechPackViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing ManageTechPackViewTest 

:General
ECHO		==========================================
ECHO		Executing IDE Login General Tests
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=IdeGeneralTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing Login General tests

:Measurement
ECHO		==========================================
ECHO		Executing MeasurementTypes View Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=MeasurementTypesViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing MeasurementTypesViewTest 

:BH
ECHO		==========================================
ECHO		Executing BusyHour View Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=BusyHoursViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing BusyHoursViewTest 

:Reference
ECHO		==========================================
ECHO		Executing ReferenceTypesView Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=ReferenceTypesViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing ReferenceTypesView tests

:Sets
ECHO		==========================================
ECHO		Executing SetsActionsSchedulingsView Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=SetsActionsSchedulingsViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing SetsActionsSchedulingsView 

:Transformers
ECHO		==========================================
ECHO		Executing TransformersView Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=TransformersViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing TransformersView Tests 

:DF
ECHO		==========================================
ECHO		Executing DataFormatsView Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=DataFormatsViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing DataFormatsView Tests 


:ES
ECHO		==========================================
ECHO		Executing ExternalStatementsView Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=ExternalStatementsViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing ExternalStatementsView Tests 

:Universe
ECHO		==========================================
ECHO		Executing UniverseView Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=UniverseViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing UniverseViewTest

:Interface
ECHO		==========================================
ECHO		Executing ManageInterfaceView Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=ManageInterfaceViewTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing ManageInterfaceViewTest

:Activate
ECHO		==========================================
ECHO		Executing TechPackActivation Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=TechPackActivationTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing TechPackActivationTest

:DeActivate
ECHO		==========================================
ECHO		Executing TechPackDeActivation Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=TechPackDeActivationTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing TechPackDeActivationTest

:Upgrade
ECHO		==========================================
ECHO		Executing TechPackUpgrade Test Cases
ECHO		==========================================
ant -file unittest_build.xml -Dclassname=TechPackUpgradeTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing TechPackUpgradeTest

:BHActivation
ECHO		==========================================
ECHO		Executing ActivateBHCriteria Test Cases
ECHO		==========================================
ECHO		To Activate BH Criteria, Tech Pack must be activated first.
ECHO		Activating test Tech Pack first........
ant -file unittest_build.xml -Dclassname=TechPackActivationTest -propertyfile ide_build.properties run_specified_test
ECHO		Now activating BusyHour Criteria.....
ant -file unittest_build.xml -Dclassname=ActivateBHCriteriaTest -propertyfile ide_build.properties run_specified_test 
ECHO		Finished executing ActivateBHCriteriaTest




:quit
ECHO.
ECHO		Exiting......
:end
