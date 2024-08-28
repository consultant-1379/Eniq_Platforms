Option Strict Off

Imports System.Collections
Imports System.Reflection.MethodBase
Imports System.Runtime.InteropServices

''
' TPUtilities class is a collection of support functions for technology package creation.
'
Public Class TPUtilitiesTPIde
    Implements ITPUtilitiesTPIde

    Public Datatype As String
    Public Datasize As String
    Public Datascale As String
    Private className As String = "TPUtilitiesTPIde.vb"

    Private Declare Function SetForegroundWindow Lib "user32" (ByVal hwnd As Integer) As Integer

    <DllImport("user32.dll", SetLastError:=True)> _
    Private Shared Function GetWindowThreadProcessId(ByVal hwnd As IntPtr, ByRef lpdwProcessId As Integer) As Integer
    End Function

    <DllImport("user32.dll")> _
    Public Shared Function AttachThreadInput(ByVal idAttach As System.UInt32, ByVal idAttachTo As System.UInt32, ByVal fAttach As Boolean) As Boolean
    End Function

    <DllImport("user32.dll", EntryPoint:="FindWindow", SetLastError:=True, CharSet:=CharSet.Auto)> _
    Private Shared Function FindWindowByCaption(ByVal zero As IntPtr, ByVal lpWindowName As String) As IntPtr
    End Function

    ' Boolean value to check if universe "Open" window was opened correctly:
    Public closeUnivWindow As Boolean = False

    Private dbProxy As DBProxy

    Public Property DataBaseProxy() As DBProxy
        Get
            Return dbProxy
        End Get
        Set(ByVal value As DBProxy)
            dbProxy = value
        End Set
    End Property

    Public Sub New()
        dbProxy = New DatabaseProxy()
    End Sub

    ''
    ' Gets data type information for given data type definition.
    ' Sets values for Datatype, Datasize and Datascale.
    '
    ' @param Value Specified the data type definition
    Public Sub getDatatype(ByRef Value As String)

        Dim TempField3 As Integer
        Dim TempField2 As String
        Dim TempField1 As String
        Dim ReplaceArg As Double
        'Datatype
        If InStrRev(UCase(Value), "UNSIGNED INT") > 0 Then
            Datatype = "unsigned int"
        ElseIf InStrRev(UCase(Value), "SMALLINT") > 0 Then
            Datatype = "smallint"
        ElseIf InStrRev(UCase(Value), "TINYINT") > 0 Then
            Datatype = "tinyint"
        ElseIf InStrRev(UCase(Value), "VARCHAR2") > 0 Then
            Datatype = "varchar2"
        ElseIf InStrRev(UCase(Value), "DOUBLE") > 0 Then
            Datatype = "double"
        ElseIf InStrRev(UCase(Value), "INT") > 0 Then
            Datatype = "int"
        ElseIf InStrRev(UCase(Value), "DATETIME") > 0 Then
            Datatype = "datetime"
        ElseIf InStrRev(UCase(Value), "DATE") > 0 Then
            Datatype = "date"
        ElseIf InStrRev(UCase(Value), "VARCHAR") > 0 Then
            Datatype = "varchar"
        ElseIf InStrRev(UCase(Value), "CHAR") > 0 Then
            Datatype = "char"
        ElseIf InStrRev(UCase(Value), "FLOAT") > 0 Then
            Datatype = "float"
        ElseIf InStrRev(UCase(Value), "LONG") > 0 Then
            Datatype = "long"
        ElseIf InStrRev(UCase(Value), "NUMERIC") > 0 Then
            Datatype = "numeric"
        Else
            Datatype = "NOT FOUND"
        End If

        'Temp Fields
        TempField3 = InStrRev(Value, "(")
        If TempField3 <= 0 Then
            TempField3 = 9999999
            TempField2 = "0"
            TempField1 = "0"
        Else
            TempField2 = Value.Substring(TempField3)
            ReplaceArg = InStrRev(TempField2, ")")
            If ReplaceArg <= 0 Then
                ReplaceArg = 9999999
            End If
            TempField1 = Replace(TempField2, ")", "")
        End If

        'Datasize
        If InStrRev(Datatype, "int") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(Datatype, "date") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(Datatype, "double") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(UCase(Datatype), "NUMERIC") > 0 Then
            If InStrRev(TempField1, ",") > 0 Then
                Datasize = Left(TempField1, InStrRev(TempField1, ",") - 1)
            Else
                If TempField1 = "0" Then
                    Datasize = "Err"
                Else
                    Datasize = TempField1
                End If
            End If

        ElseIf InStrRev(UCase(Datatype), "CHAR") > 0 Then
            If TempField1 = "0" Then
                Datasize = "Err"
            Else
                Datasize = TempField1
            End If
        Else
            Datasize = "0"
        End If
        'Datascale
        If InStrRev(UCase(Datatype), "FLOAT") > 0 Then
            Datascale = "0"
        ElseIf InStrRev(UCase(Datatype), "DOUBLE") > 0 Then
            Datascale = "0"
        ElseIf InStrRev(UCase(Datatype), "NUMERIC") > 0 Then
            If InStrRev(TempField1, ",") > 0 Then
                Datascale = Right(TempField1, Len(TempField1) - InStrRev(TempField1, ","))
            Else
                Datascale = "0"
            End If
        Else
            Datascale = "0"
        End If

    End Sub

    ''
    ' Reads single value from TP definition.
    '
    ' @param testSheet Specified the value's location in definition
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @param TechPackTPIde specifies used Techpack
    ' @return Value read from TP Definition   
    Public Function readSingleValue(ByRef testSheet As String, ByRef conn As System.Data.Odbc.OdbcConnection, ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, ByRef TechPackTPIde As String) As String


        Dim Value As String
        Dim tmpValue As String
        Dim prevValue As String

        Dim tmpTechPackTPIde As String

        tmpTechPackTPIde = "'" & TechPackTPIde & "'"

        Try
            Dim sqlString As String
            sqlString = testSheet & tmpTechPackTPIde
            dbCommand = New System.Data.Odbc.OdbcCommand(sqlString, conn)
            dbReader = dbCommand.ExecuteReader()

            While (dbReader.Read())
                Value = Trim(dbReader.GetValue(0).ToString())


                If (tmpValue <> "" And Value <> prevValue) Then
                    tmpValue = tmpValue & "," & Value
                    prevValue = Value
                Else
                    tmpValue = Value
                    prevValue = Value
                End If

            End While
            dbReader.Close()
            dbCommand.Dispose()
        Catch ex As Exception
            Try
                dbReader.Close()
                dbCommand.Dispose()
                Return Nothing
            Catch ee As Exception
                Return Nothing
            End Try
        End Try

        Return tmpValue

    End Function

    Public Function readSingleValue(ByRef testSheet As String, ByRef conn As System.Data.Odbc.OdbcConnection, ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader) As String


        Dim Value As String
        Dim tmpValue As String
        Dim prevValue As String


        Try
            Dim sqlString As String
            sqlString = testSheet
            dbCommand = New System.Data.Odbc.OdbcCommand(sqlString, conn)

            If dbReader.IsClosed = False Then
                dbReader.Close()
            End If
            dbReader = dbCommand.ExecuteReader()

            While (dbReader.Read())
                Value = Trim(dbReader.GetValue(0).ToString())


                If (tmpValue <> "" And Value <> prevValue) Then
                    tmpValue = tmpValue & "," & Value
                    prevValue = Value
                Else
                    tmpValue = Value
                    prevValue = Value
                End If

            End While
            dbReader.Close()
            dbCommand.Dispose()
        Catch ex As Exception
            Try
                dbReader.Close()
                dbCommand.Dispose()
                Return Nothing
            Catch ee As Exception
                Return Nothing
            End Try
        End Try

        Return tmpValue

    End Function

    ''
    'Gets a list of the ranking measurement types (Object and Element RANKBH tables)
    '
    ' WI 2.10
    '@param mts List of measurement type objects
    '@return A list of the ranking measurement types
    Public Function getRankMeasurementTypes(ByVal mts As MeasurementTypesTPIde) As ArrayList Implements ITPUtilitiesTPIde.getRankMeasurementTypes
        Dim rankMTs As New ArrayList()
        Dim unvMt As MeasurementTypesTPIde.MeasurementType
        Dim Count As Integer

        ' Get the rank tables:
        For Count = 1 To mts.Count
            unvMt = mts.Item(Count)
            ' Check the unvMt is not a null object:
            If Not unvMt Is Nothing Then
                Dim name As String
                name = unvMt.TypeName

                If unvMt.RankTable = True Then
                    If name <> "" AndAlso name <> Nothing AndAlso (rankMTs.Contains(name) = False) Then
                        rankMTs.Add(unvMt)
                    End If
                End If
            End If
        Next
        Return rankMTs
    End Function

    ''
    ' Sets up database reader with SQL statement
    ' 
    ' WI 2.10
    '@param sqlCommand SQL command to pass to the database
    '@param conn Connection to the database
    '@return OdbcDataReader The reader 
    Public Function setupDatabaseReader(ByRef sqlCommand As String, ByRef conn As System.Data.Odbc.OdbcConnection) As System.Data.Odbc.OdbcDataReader
        Dim dbReader As System.Data.Odbc.OdbcDataReader
        Dim dbCommand As System.Data.Odbc.OdbcCommand
        dbCommand = New System.Data.Odbc.OdbcCommand(sqlCommand, conn)

        Try
            'If dbReader.IsClosed = False Then
            'Trace.Writeline("Closing database")
            'dbReader.Close()
            'End If
            dbReader = dbCommand.ExecuteReader()
        Catch ex As Exception
            Throw New Exception("Database Exception: " & ex.ToString)
        End Try
        Return dbReader
    End Function

    ''
    'Gets a measurement type by name from a list of measurement types.
    '@param     mtNameToLookFor     The measurement type name to look for.
    '@param     mts                 The list of measurement types for the current universe.
    '@returns   mType               A MeasurementTypesTPIde.MeasurementType object from the list of measurement types.
    '                               Returns Nothing if the measurement type is not found.
    Public Function getMeasurementTypeByName(ByVal mtNameToLookFor As String, ByRef mts As MeasurementTypesTPIde) _
    As MeasurementTypesTPIde.MeasurementType Implements ITPUtilitiesTPIde.getMeasurementTypeByName

        Dim rankMTs As New ArrayList()
        Dim unvMt As MeasurementTypesTPIde.MeasurementType
        Dim Count As Integer
        ' The measurment type to be returned, default value of Nothing:
        Dim mType As MeasurementTypesTPIde.MeasurementType = Nothing

        If Not (mts Is Nothing) Then
            ' Iterate through the measurement types:
            For Count = 1 To mts.Count
                unvMt = mts.Item(Count)
                ' Check the unvMt is not a null object:
                If Not (unvMt Is Nothing) Then
                    Dim unvMtName As String
                    unvMtName = unvMt.TypeName

                    If unvMtName <> "" And Not (unvMtName Is Nothing) Then
                        If unvMtName = mtNameToLookFor Then
                            mType = unvMt
                            Exit For
                        End If
                    End If
                End If
            Next
        End If

        Return mType
    End Function

    ' Get the BHTARGETTYPE. This gets the target types for a given place holder
    '
    '@param techpackVersion
    '@param rankMeasType
    '@param tpConn
    '@param techpackIde
    '@returns An ArrayList with the target types as Strings.
    Public Function getBHTargetTypes(ByVal techpackVersion As String, ByVal rankMeasType As MeasurementTypesTPIde.MeasurementType, _
                                     ByRef tpConn As System.Data.Odbc.OdbcConnection) As ArrayList Implements ITPUtilitiesTPIde.getBHTargetTypes
        Dim sqlStatement As String
        Dim targetTypes As ArrayList
        sqlStatement = "Select DISTINCT BHTARGETTYPE from BusyhourMapping where VERSIONID = '" & techpackVersion & _
        "' AND BHLEVEL = '" & rankMeasType.TypeName & "'"
        dbProxy.setupDatabaseReader(sqlStatement, tpConn)
        targetTypes = dbProxy.readSingleColumnFromDB(sqlStatement, False)
        Return targetTypes
    End Function

    ' Get the busy hour criteria:
    Public Function getBHCriteria(ByVal techpackVersion As String, ByVal rankMeasType As MeasurementTypesTPIde.MeasurementType, _
                                 ByRef tpConn As System.Data.Odbc.OdbcConnection, ByVal bhType As String, _
                                 ByVal placeholderID As String) As ArrayList
        Dim sqlStatement As String
        Dim criteria As ArrayList
        sqlStatement = "Select BHCRITERIA from BusyhourMapping where VERSIONID = '" & techpackVersion & _
        "' AND BHLEVEL = '" & rankMeasType.TypeName & "'" & " AND " & bhType & " = '" & placeholderID & "'"
        dbProxy.setupDatabaseReader(sqlStatement, tpConn)
        criteria = dbProxy.readSingleColumnFromDB(sqlStatement, False)
        Return criteria
    End Function

    ' Get the busy hour criteria (the formula):
    Public Function getBHAggregationType(ByVal techpackVersion As String, ByVal rankMeasType As MeasurementTypesTPIde.MeasurementType, _
                                 ByRef tpConn As System.Data.Odbc.OdbcConnection, ByVal bhType As String, _
                                 ByVal placeholderID As String) As ArrayList
        Dim sqlStatement As String
        Dim aggTypes As ArrayList
        sqlStatement = "Select AGGREGATIONTYPE from BusyhourMapping where VERSIONID = '" & techpackVersion & _
        "' AND BHLEVEL = '" & rankMeasType.TypeName & "'" & " AND " & bhType & " = '" & placeholderID & "'"
        dbProxy.setupDatabaseReader(sqlStatement, tpConn)
        aggTypes = dbProxy.readSingleColumnFromDB(sqlStatement, False)
        Return aggTypes
    End Function

    ' Get the busy hour description:
    Public Function getBHDescription(ByVal techpackVersion As String, ByVal rankMeasType As MeasurementTypesTPIde.MeasurementType, _
                             ByRef tpConn As System.Data.Odbc.OdbcConnection, ByVal bhType As String, _
                             ByVal placeholderID As String) As ArrayList
        Dim sqlStatement As String
        Dim descriptions As ArrayList
        sqlStatement = "Select DESCRIPTION from BusyhourMapping where VERSIONID = '" & techpackVersion & _
        "' AND BHLEVEL = '" & rankMeasType.TypeName & "'" & " AND " & bhType & " = '" & placeholderID & "'"
        dbProxy.setupDatabaseReader(sqlStatement, tpConn)
        descriptions = dbProxy.readSingleColumnFromDB(sqlStatement, False)
        Return descriptions
    End Function

    ' Get the busy hour grouping:
    Public Function getGrouping(ByVal techpackVersion As String, ByVal rankMeasType As MeasurementTypesTPIde.MeasurementType, _
                         ByRef tpConn As System.Data.Odbc.OdbcConnection, ByVal bhType As String, _
                         ByVal placeholderID As String) As ArrayList
        Dim sqlStatement As String
        Dim groupings As ArrayList
        sqlStatement = "Select GROUPING from BusyhourMapping where VERSIONID = '" & techpackVersion & _
        "' AND BHLEVEL = '" & rankMeasType.TypeName & "'" & " AND " & bhType & " = '" & placeholderID & "'"
        dbProxy.setupDatabaseReader(sqlStatement, tpConn)
        groupings = dbProxy.readSingleColumnFromDB(sqlStatement, False)
        Return groupings
    End Function

    ''
    'Checks if placeholder busy hour functionality is enabled.
    '@param techpackVersion
    '@param rankMeasType
    '@param tpConn
    '@param databaseFacade
    '@param techpackIde
    '@returns True if additional busy hour functionality has been enabled.
    Public Function checkBusyHourFunctionality(ByVal techpackVersion As String, ByRef tpConn As System.Data.Odbc.OdbcConnection) As Boolean
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " Entering")

        ' Boolean to check if the enhanced busy hour functionality is enabled:
        Dim enabled As Boolean
        enabled = False

        Try
            ' Define SQL string to check busy hour mappings:
            Dim sqlStatement As String
            sqlStatement = "Select BHLEVEL from BusyhourMapping where VERSIONID = '" & techpackVersion & "'"
            ' ArrayList to hold list of placeholders read from database:
            Dim placeholders As ArrayList
            dbProxy.setupDatabaseReader(sqlStatement, tpConn)
            placeholders = dbProxy.readSingleColumnFromDB(sqlStatement, False)
            If (placeholders.Count > 0) Then
                Dim message As String
                message = "Found " & placeholders.Count & " busy hour placeholders for " & techpackVersion & ", " & " additional busy hour functionality is enabled."
                Console.WriteLine(message)
                Trace.WriteLine(message)
                enabled = True
            End If
        Catch odbcException As System.Data.Odbc.OdbcException
            enabled = False
            Trace.WriteLine("Placeholder busy hour functionality not enabled for this tech pack: " & techpackVersion)
        Catch ex As Exception
            enabled = False
            Trace.WriteLine("Placeholder busy hour functionality not enabled for this tech pack: " & techpackVersion)
        End Try
        Trace.WriteLine(classNameAndFunction & " Exiting, enabled = " & enabled)
        Return enabled
    End Function

    ''
    ' Protected method to display a message box. Can be overridden to return test values for the MsgBoxResult. 
    ' 
    '@param message The message string to display.
    '@param msgBoxStyle The style of the message box.
    '@returns A MsgBoxResult - the result depending on what the user clicks.
    Public Overridable Function displayMessageBox(ByVal message As String, ByVal msgBoxStyle As MsgBoxStyle, _
                                                  ByVal msgBoxTitle As String) As MsgBoxResult Implements ITPUtilitiesTPIde.displayMessageBox
        Dim msgResult As MsgBoxResult
        msgResult = MsgBox(message, msgBoxStyle, msgBoxTitle)
        Return msgResult
    End Function

    ''
    'Creates a new instance of the Designer.Application and logs on.
    '@param BoVersion The BO version.
    '@param boUser Username for logging on to Designer.
    '@param boPass Password for logging on to Designer.
    '@param boRep The repository string for logging on.
    '@param BoAut Authorisation for logging on to Designer (eg. ENTERPRISE)
    '@returns A new Designer.IApplication instance.
    ' 
    '@remarks First tries to log on using details supplied, if this fails it displays the logon dialog box to ask user for details.
    Public Function setupDesignerApp(ByVal BoVersion As String, ByVal boUser As String, ByVal boPass As String, ByVal boRep As String, _
                               ByVal BoAut As String) As Designer.IApplication Implements ITPUtilitiesTPIde.setupDesignerApp
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " Entering")

        ' Define new Designer.Application:
        Dim DesignerApp As Designer.IApplication = createDesignerApp()

        Try
            ' Try to log on:
            logonToDesignerApp(BoVersion, boUser, boPass, boRep, BoAut, DesignerApp)
        Catch invalidBOex As InvalidBOVersionException
            ' Invalid BO Version, quit:
            Console.WriteLine(classNameAndFunction & ": Error logging on to Designer application - invalid BO version supplied: " & BoVersion)
            Throw New Exception("Error logging on to Designer application - invalid BO version supplied: " & BoVersion, invalidBOex)
        Catch ex As Exception
            Trace.WriteLine(classNameAndFunction & "Error logging on to Designer application: " & ex.ToString())
            'Do a manual logon if there was a general error logging on to the Designer:
            Try
                logonToDesignerAppManually(DesignerApp)
            Catch manualLogonExc As Exception
                ' Failed to log on manually, quit:
                Trace.WriteLine(classNameAndFunction & manualLogonExc.ToString())
                Throw New Exception("Error logging on to Designer application and failed to log on manually")
            End Try
        End Try
        Trace.WriteLine(classNameAndFunction & " Exiting")
        Return DesignerApp
    End Function


    ''
    ' Log on to the designer application.
    ' 
    '@param BoVersion
    '@param boUser
    '@param boPass
    '@param boRep
    '@param BoAut
    '@returns Designer.Application
    Public Sub logonToDesignerApp(ByVal BoVersion As String, ByVal boUser As String, ByVal boPass As String, ByVal boRep As String, _
                               ByVal BoAut As String, ByRef DesignerApp As Designer.IApplication)
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " entering")
        DesignerApp.Visible = False
        If BoVersion = "6.5" Then
            DesignerApp.LoginAs(boUser, boPass, False, boRep)
        ElseIf BoVersion = "XI" Then
            Trace.WriteLine(classNameAndFunction & ": " & DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsDesignerDirectory))
            Trace.WriteLine(classNameAndFunction & ": " & DesignerApp.Version)
            ' e.g. DesignerApp.Logon("Administrator", "", "dcweb4-a:6400", "Enterprise")
            DesignerApp.Logon(boUser, boPass, boRep, BoAut)
        Else
            Trace.WriteLine("TPUtilities, logonToDesignerApp(): " & "Invalid BO Version. Contact ENIQ support.")
            Console.WriteLine("TPUtilities, logonToDesignerApp(): " & "Invalid BO Version. Contact ENIQ support.")
            ' Throw exception:
            Throw New InvalidBOVersionException("TPUtilities, logonToDesignerApp(): " & "Invalid BO Version. Contact ENIQ support.")
        End If
        Trace.WriteLine(classNameAndFunction & " exiting")
    End Sub

    ''
    'Logs on to Designer manually. 
    '@param DesignerApp
    '@returns
    Public Sub logonToDesignerAppManually(ByVal DesignerApp As Designer.IApplication)
        Try
            Console.WriteLine("Problem with the BO Login Credentials you have supplied :" + "Using manual logon")
            Trace.WriteLine("TPUtilities, logonToDesignerAppManually(): " & "Using manual logon.")
            DesignerApp.LogonDialog()
        Catch e As Exception
            Try
                Trace.WriteLine("TPUtilities, logonToDesignerAppManually(): " & "Calling quit() on Designer application")
                DesignerApp.Quit()
            Catch ee As Exception
                Trace.WriteLine("TPUtilities, logonToDesignerAppManually(): " & "Error closing Designer application.")
            End Try
            Throw New Exception("TPUtilities, logonToDesignerAppManually(): " & "Failed to log on to Designer application manually, exiting.")
        End Try
    End Sub

    ''
    'Calls Designer to open a universe.
    '@param     DesignerApp     Reference to the Designer application.
    '@returns   newUniverse     The universe that is opened
    Public Function openUniverse(ByRef DesignerApp As Designer.IApplication) As Designer.IUniverse
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & ": Entering")
        Dim retry As Boolean = True
        Dim newUniverse As Designer.IUniverse = Nothing
        Dim tryCount As Integer = 0
        While retry = True
            Try
                retry = False
                DesignerApp.Interactive = False
                newUniverse = doUniverseOpen(DesignerApp)
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                tryCount += 1
                If (tryCount = 2) Then
                    retry = False
                    Trace.WriteLine(classNameAndFunction & ": Couldn't open universe. Reached maximum retry limit. Could not retry more...")
                    Console.WriteLine("Couldn't open universe. Reached maximum retry limit. Could not retry more...")
                    closeUnivWindow = True ' Notifying the Thread  windowCheck() to complete which is running in background
                    System.Threading.Thread.Sleep(600) ' Giving the time to let thread windowCheck() to die ( 501 miliseconds is enough but giving 600 to be on the safer side )
                Else
                    Trace.WriteLine(classNameAndFunction & ": Couldn't open universe. Exception: " & ex.ToString & vbCr & " Retrying...")
                    Console.WriteLine("Couldn't open universe. Exception: " & ex.ToString & vbCr & " Retrying...")
                    retry = True
                End If
            End Try
        End While
        Trace.WriteLine(classNameAndFunction & ": Exiting")
        Return newUniverse
    End Function

    'Prompts the user to open a universe. Checks universe name fully.
    '
    '@param UniverseExtension
    '@param UniverseNameExtension
    '@param BoVersion 
    '@param DesignerApp
    '@returns newUniverse A reference to the universe just opened.
    Public Overridable Function promptToOpenUniverse(ByRef UniverseNameExtension As String, ByVal UniverseExtension As String, ByVal BoVersion As String, _
                                   ByRef DesignerApp As Designer.IApplication, ByVal UniverseName As String, _
                                   ByVal UniverseFileName As String, ByVal outputFolder As String) As Designer.IUniverse Implements ITPUtilitiesTPIde.promptToOpenUniverse
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " entering")
        Dim newUniverse As Designer.IUniverse = Nothing
        Dim retry As Boolean
        Dim Message As String

        DesignerApp.Visible = False
        DesignerApp.Interactive = False

        Trace.WriteLine(classNameAndFunction & "Prompting user to open universe")
        If UniverseNameExtension <> "" Then
            Message = "Please open Universe '" & UniverseName & " " & UniverseNameExtension & "'"
        Else
            Message = "Please open Universe '" & UniverseName & "'"
        End If
        ' If BO version is 6.5 add filename:
        If BoVersion = "6.5" Then
            If UniverseExtension <> "" Then
                Message &= " with filename '" & UniverseFileName & UniverseExtension & "'."
            Else
                Message &= " with filename '" & UniverseFileName & "'."
            End If
        End If
        Console.WriteLine(Message)
        retry = True
        Dim tryCount As Integer = 0
        While retry = True
            Try
                retry = False
                newUniverse = doUniverseOpen(DesignerApp)
                ' Check that the universe opened is the correct one (only check for BO XI):
                If (BoVersion = "XI") Then
                    Dim nameIsOk As Boolean = False
                    nameIsOk = checkUniverseName(newUniverse, UniverseFileName, UniverseName, UniverseNameExtension)
                    If (nameIsOk = False) Then
                        retry = True
                    Else
                        Trace.WriteLine(classNameAndFunction & ": Opened universe is correct. ")
                        Console.WriteLine("Opened universe is correct.")
                    End If
                End If
            Catch ex As Exception
                System.Threading.Thread.Sleep(5000)
                tryCount += 1
                If (tryCount = 2) Then
                    retry = False
                    Trace.WriteLine(classNameAndFunction & ": Couldn't open universe. Reached maximum retry limit. Could not retry more...")
                    Console.WriteLine("Couldn't open universe. Reached maximum retry limit. Could not retry more...")
                    closeUnivWindow = True ' Notifying the Thread  windowCheck() to complete which is running in background
                    System.Threading.Thread.Sleep(600) ' Giving the time to let thread windowCheck() to die ( 501 miliseconds is enough but giving 600 to be on the safer side )
                Else
                    Trace.WriteLine(classNameAndFunction & ": Couldn't open universe. Exception: " & ex.ToString & vbCr & " Retrying one more time...")
                    Console.WriteLine("Couldn't open universe. Exception: " & ex.ToString & vbCr & " Retrying one more time...")
                    retry = True
                End If
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Visible = False
                DesignerApp.Interactive = False
            Catch ex As Exception
                Trace.WriteLine(classNameAndFunction & " Failed making designer app non visible and non-interactive, retrying...")
                System.Threading.Thread.Sleep(5000)
                retry = True
            End Try
        End While
        ' Check if the universe opening failed:
        If (newUniverse Is Nothing) Then
            Throw New Exception(classNameAndFunction & "Failed to open universe.")
        End If

        ' Return the universe that has just been opened:
        Trace.WriteLine(classNameAndFunction & " exiting")
        Return newUniverse
    End Function

    ''
    'Calls Designer to open a universe. This is a protected function so it can be overridden for tests.
    '@param     DesignerApp     Reference to the Designer application.
    '@returns   newUniverse     The universe that is opened
    Protected Overridable Function doUniverseOpen(ByRef DesignerApp As Designer.IApplication) As Designer.IUniverse
        'Launch window check in a separate thread:
        Dim Thread1 As New System.Threading.Thread(AddressOf windowCheck)
        Thread1.Start()

        closeUnivWindow = False
        Dim newUniverse As Designer.IUniverse = Nothing
        newUniverse = DesignerApp.Universes.Open()
        closeUnivWindow = True

        Return newUniverse
    End Function

    ''
    'Checks for the Universe Wizard window opened by the IDE, and 
    'puts the Universe Designer "Open" window on top of it
    ''
    Private Sub windowCheck()
        Dim retry As Boolean = True
        Dim tryCount As Integer = 0
        While retry = True
            Try
                tryCount += 1
                If (tryCount = 4) Then
                    retry = False
                End If
                ' Define names of the IDE window:
                System.Threading.Thread.Sleep(2000)
                Dim lpszParentClass As String = "ENIQ Technology Package IDE"
                Dim lpszParentWindow As String = "Universe Wizard"

                Dim ParenthWnd As New IntPtr(0)
                Dim hWnd As New IntPtr(0)
                ' Find the Universe Open window by its caption:
                ParenthWnd = FindWindowByCaption(IntPtr.Zero, "Universe Wizard")
                hWnd = FindWindowByCaption(IntPtr.Zero, "Open")
                Dim pointer1 As Integer = GetWindowThreadProcessId(ParenthWnd, Nothing)
                Dim pointer2 As Integer = GetWindowThreadProcessId(hWnd, Nothing)

                ' Attach the current thread to the IDE window's thread:
                AttachThreadInput(ParenthWnd, hWnd, True)
                SetForegroundWindow(hWnd)
                Trace.WriteLine("Set Universe Open window to foreground")

                ' Wait until the universe has been opened:
                Do Until closeUnivWindow = True
                    System.Threading.Thread.Sleep(500)
                Loop

                ' Detach the current thread from the IDE window's thread:
                AttachThreadInput(ParenthWnd, hWnd, False)
                retry = False
            Catch ex As Exception
                Console.WriteLine("Error setting foreground window: " & ex.ToString())
                System.Threading.Thread.Sleep(2000)
            End Try
        End While
    End Sub


    ''' <summary>
    ''' 
    ''' </summary>
    ''' <returns></returns>
    ''' <remarks></remarks>
    Protected Overridable Function createDesignerApp() As Designer.IApplication
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & ": Entering. Getting instance of Designer.Application")
        Dim DesignerApp As Designer.IApplication = Nothing
        Try
            DesignerApp = New Designer.Application
        Catch ex As Exception
            Trace.WriteLine(classNameAndFunction & "Exception getting Designer.Application: " + ex.ToString)
        End Try
        Trace.WriteLine(classNameAndFunction & ": Exiting. Got instance of Designer.Application")
        Return DesignerApp
    End Function

    ''
    'Checks if the universe opened by the user has the correct name.
    'Checks the short name used in BO version 6.5, universe Name and universe LongName.
    '@param     openedUniverse      Reference to the universe the user has opened
    '@param     expectedShortName   The universe's short name (e.g. DCE1)
    '@param     expectedName        The name of the universe we are expecting
    '@param     expectedExtension   The universe extension we are expecting
    '@returns   Boolean             True if the universe name is correct, or if it is not defined (empty string).
    Public Function checkUniverseName(ByVal openedUniverse As Designer.IUniverse, ByVal expectedShortName As String, _
                                      ByVal expectedName As String, ByVal expectedExtension As String) As Boolean
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & ": Entering. Checking universe name")
        ' By default nameIsOk is True:
        Dim nameIsOk As Boolean = True
        Dim expectedNameWithExtension As String
        Dim message As String = ""

        Try
            Trace.WriteLine("UniverseFunctionsTPIde, checkUniverseName(): expectedShortName = " & expectedShortName)
            Trace.WriteLine("UniverseFunctionsTPIde, checkUniverseName(): expectedName = " & expectedName)
            Trace.WriteLine("UniverseFunctionsTPIde, checkUniverseName(): expectedExtension = " & expectedExtension)

            ' Get the expected name with its extension:
            expectedNameWithExtension = expectedName & " " & expectedExtension

            If (openedUniverse.Name = "" And openedUniverse.LongName = "" And expectedShortName = "") Then
                Trace.WriteLine("UniverseFunctionsTPIde, checkUniverseName(): Universe name was not defined.")
                ' Universe name is not set:
                message = "The universe that was opened has no name parameter defined. Are you sure you want to use this universe?"
                nameIsOk = checkUniverseNameWithUser(message)
            ElseIf (openedUniverse.Name <> expectedShortName AndAlso openedUniverse.LongName <> expectedShortName _
                    AndAlso openedUniverse.Name <> expectedName AndAlso openedUniverse.Name <> expectedNameWithExtension _
                    AndAlso openedUniverse.LongName <> expectedName AndAlso openedUniverse.LongName <> expectedNameWithExtension) Then
                message = "The name of the universe that was opened: (" & openedUniverse.Name & ") does not match the expected name: " _
                & expectedNameWithExtension & ". Are you sure you want to use this universe?"
                nameIsOk = checkUniverseNameWithUser(message)
            End If
        Catch ex As Exception
            Trace.WriteLine("Error checking universe name: " & ex.ToString())
        End Try

        Trace.WriteLine(classNameAndFunction & ": Exiting. nameIsOk: " & nameIsOk)
        Return nameIsOk
    End Function

    ''
    'Displays a message to the user to check if they want to open the universe.
    'Called if the universe name is not what we expect.
    '@param     message     Message to show in the message box.
    '@returns   Boolean     True if the user clicks Ok.
    Private Function checkUniverseNameWithUser(ByVal message As String) As Boolean
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & ": Entering. Checking universe name")
        Dim nameIsOk As Boolean = True
        ' Ask user if they are sure they want to use this universe:
        Dim choice As MsgBoxResult = displayMessageBox(message, MsgBoxStyle.YesNo, "Checking universe name")
        Console.WriteLine(message)
        If (choice = MsgBoxResult.Yes) Then
            nameIsOk = True
        ElseIf (choice = MsgBoxResult.No) Then
            nameIsOk = False
        End If
        Trace.WriteLine(classNameAndFunction & ": Exiting. nameIsOk: " & nameIsOk)
        Return nameIsOk
    End Function

    ''' 
    ''' Function to check whether the given MeasurementTypeIs and dataName is present in MeasurementVector table or not
    ''' param measurementTypeId
    ''' param dataName
    ''' @param tpConn
    ''' @returns
    Public Function isVectorRangePresent(ByVal measurementTypeId As String, ByVal dataName As String, ByVal tpConn As System.Data.Odbc.OdbcConnection)
        Dim sqlStatement As String
        Dim targetTypes As ArrayList
        sqlStatement = "Select TYPEID from MeasurementVector where TYPEID = '" & measurementTypeId & "' AND DATANAME ='" & dataName & "'"
        dbProxy.setupDatabaseReader(sqlStatement, tpConn)
        targetTypes = dbProxy.readSingleColumnFromDB(sqlStatement, False)

        If Not (targetTypes Is Nothing) Then
            If (targetTypes.Count < 1) Then
                Return False
            Else
                Return True
            End If
        Else
            Trace.WriteLine("No entry found in database for mesaurementTypeId: " & measurementTypeId & " & dataName: " & dataName)
            Return False
        End If

    End Function

End Class

