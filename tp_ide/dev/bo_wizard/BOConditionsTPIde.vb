Option Strict Off

Imports System.Collections

''
'  BOConditions class is a collection of BOCondition classes
'
Public Class BOConditionsTPIde
    'Public Class BOConditions
    Private _conditions As System.Collections.ArrayList = New System.Collections.ArrayList
    Public ConditionParse As Boolean
    Private universeProxy As IUniverseProxy
    Private databaseProxy As DBProxy

    Dim Cond As Designer.PredefinedCondition
    Dim Obj As Designer.IObject
    Dim Cls As Designer.IClass    

    Dim condName As String
    Dim objName As String
    Dim condWhere As String
    Dim unvcondition As BOCondition

    Dim autoGenerate As String
    Dim condObjClass As String
    Dim condObject As String
    Dim promptText As String
    Dim multiSelection As String
    Dim freeText As String

    Public Sub New(ByVal proxy As IUniverseProxy)
        Me.universeProxy = proxy
    End Sub

    ''
    '  Gets count of BOCondition classes in BOConditions class
    '
    ' @param Index Specifies the index in the BOConditions class
    ' @return Count of BOCondition classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _conditions Is Nothing) Then
                Return _conditions.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOCondition class from BOConditions class based on given index.
    '
    ' @param Index Specifies the index in the BOConditions class
    ' @return Reference to BOCondition
    Public ReadOnly Property Item(ByVal Index As Integer) As BOCondition
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_conditions.Item(Index - 1), BOCondition)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BOCondition class to BOConditions class
    '
    ' @param ValueIn Specifies reference to BOCondition
    Public Sub AddItem(ByVal ValueIn As BOCondition)

        If (Not _conditions Is Nothing) Then
            _conditions.Add(ValueIn)
        End If

    End Sub

    ''
    'Protected creator method to instantiate TPUtilitiesTPIde.
    'Can be overridden in tests.
    '@returns A new TPUtilitiesTPIde object
    Protected Overridable Function createTPUtilities() As ITPUtilitiesTPIde
        Return New TPUtilitiesTPIde()
    End Function

    ''
    'Protected creator method to create a DatabaseProxy.
    'Can be overridden in tests.
    '@returns A new DatabaseProxy object
    Protected Overridable Function createDatabaseProxy() As DBProxy
        Return New DatabaseProxy()
    End Function

    ''
    '  BOCondition class defines conditions for universe.
    '
    Public Class BOCondition
        Private m_ClassName As String
        Private m_OldClassName As String
        Private m_Name As String
        Private m_OldName As String
        Private m_Description As String
        Private m_Path As String
        Private m_Table As String
        Private m_Header As String
        Private m_Level As Integer
        Private m_ElementBHRelated As Boolean
        Private m_ObjectBHRelated As Boolean
        Private m_UniverseExtension As String

        Public Property UniverseExtension()
            Get
                UniverseExtension = m_UniverseExtension
            End Get

            Set(ByVal Value)
                m_UniverseExtension = LCase(Value)
            End Set

        End Property
        ''
        ' Gets and sets value for ClassName parameter. 
        ' ClassName defines condition's class name.
        '
        ' @param Value Specifies value of ClassName parameter
        ' @return Value of ClassName parameter
        Public Property ClassName()
            Get
                ClassName = m_ClassName
            End Get

            Set(ByVal Value)
                m_ClassName = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for OldClassName parameter. 
        ' OldClassName defines condition's previous class name.
        '
        ' @param Value Specifies value of OldClassName parameter
        ' @return Value of OldClassName parameter
        Public Property OldClassName()
            Get
                OldClassName = m_OldClassName
            End Get

            Set(ByVal Value)
                m_OldClassName = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Level parameter. 
        ' Level defines condition's class level.
        '
        ' @param Value Specifies value of Level parameter
        ' @return Value of Level parameter
        Public Property Level()
            Get
                Level = m_Level
            End Get

            Set(ByVal Value)
                m_Level = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Name parameter. 
        ' Name defines condition's name.
        '
        ' @param Value Specifies value of Name parameter
        ' @return Value of Name parameter
        Public Property Name()
            Get
                Name = m_Name
            End Get

            Set(ByVal Value)
                m_Name = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for OldName parameter. 
        ' OldName defines condition's previous name.
        '
        ' @param Value Specifies value of OldName parameter
        ' @return Value of OldName parameter
        Public Property OldName()
            Get
                OldName = m_OldName
            End Get

            Set(ByVal Value)
                m_OldName = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Description parameter. 
        ' Description defines description.
        '
        ' @param Value Specifies value of Description parameter
        ' @return Value of Description parameter
        Public Property Description()
            Get
                Description = m_Description
            End Get

            Set(ByVal Value)
                m_Description = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Path parameter. 
        ' Path defines condition's path.
        '
        ' @param Value Specifies value of Path parameter
        ' @return Value of Path parameter
        Public Property Path()
            Get
                Path = m_Path
            End Get

            Set(ByVal Value)
                m_Path = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Table parameter. 
        ' Table defines condition's table.
        '
        ' @param Value Specifies value of Table parameter
        ' @return Value of Table parameter
        Public Property Table()
            Get
                Table = m_Table
            End Get

            Set(ByVal Value)
                m_Table = Trim(Value)
            End Set

        End Property

        ''
        ' Gets and sets value for Header parameter. 
        ' Header defines condition's header.
        '
        ' @param Value Specifies value of Header parameter
        ' @return Value of Header parameter
        Public Property Header()
            Get
                Header = m_Header
            End Get

            Set(ByVal Value)
                m_Header = Trim(Value)
            End Set

        End Property

        Public Property ElementBHRelated()
            Get
                ElementBHRelated = m_ElementBHRelated
            End Get

            Set(ByVal Value)
                If LCase(Trim(Value)) = "1" Then
                    m_ElementBHRelated = True
                Else
                    m_ElementBHRelated = False
                End If
            End Set

        End Property

        Public Property ObjectBHRelated()
            Get
                ObjectBHRelated = m_ObjectBHRelated
            End Get

            Set(ByVal Value)
                If LCase(Trim(Value)) = "1" Then
                    m_ObjectBHRelated = True
                Else
                    m_ObjectBHRelated = False
                End If
            End Set

        End Property

    End Class
    ''
    ' Adds extra condition to universe. 
    '
    ' @param tp_name Specifies name of tech pack
    ' @param Univ Specifies reference to universe
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @remarks Conditions are defined in TP definition's sheet 'Universe conditions'.
    Public Function addConditions(ByRef tp_name As String, ByRef conn As System.Data.Odbc.OdbcConnection, _
                                  ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                                  ByRef mts As MeasurementTypesTPIde, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, _
                                  ByRef UniverseNameExtension As String, ByRef TechPackTPIde As String) As Boolean

        Dim unvcond As String
        unvcond = "SELECT CLASSNAME,UNIVERSEEXTENSION,UNIVERSECONDITION," & _
        "SUBSTR(DESCRIPTION,1,8000),SUBSTR(DESCRIPTION,8001,8000),SUBSTR(DESCRIPTION,16001,8000),SUBSTR(DESCRIPTION,24001,8000)," & _
        "SUBSTR(CONDWHERE,1,8000),SUBSTR(CONDWHERE,8001,8000),SUBSTR(CONDWHERE,16001,8000),SUBSTR(CONDWHERE,24001,8000)" & _
        ",AUTOGENERATE,CONDOBJCLASS,CONDOBJECT,PROMPTTEXT,MULTISELECTION,FREETEXT,OBJ_BH_REL,ELEM_BH_REL FROM Universecondition WHERE VERSIONID='" & TechPackTPIde & "'"

        databaseProxy = createDatabaseProxy()
        databaseProxy.closeDatabase()
        databaseProxy.closeConnection(conn)
        databaseProxy.openConnection(conn)

        Try
            databaseProxy.setupDatabaseReader(unvcond, conn)
        Catch ex As Exception
            Console.WriteLine("Error: failed to read objects from universe : " & ex.ToString)
            Trace.WriteLine("Error: failed to read objects from universe : " & ex.ToString)
            Return False
        End Try


        While (databaseProxy.read())

            autoGenerate = ""
            condObjClass = ""
            condObject = ""
            promptText = ""
            multiSelection = ""
            freeText = ""
            condWhere = ""

            If databaseProxy.getValue(2).ToString() <> "" Then
                Dim addedCondition As Boolean = setupCondition(UniverseNameExtension, mts, tp_name, ObjectBHSupport, ElementBHSupport)
                If (addedCondition = False) Then
                    Trace.WriteLine("Failed to add condition")
                End If
            End If

        End While
        databaseProxy.closeDatabase()

        Return True

    End Function

    ''' Sets up the condition and adds it to the universe.
    ''' 
    ''' @param UniverseNameExtension
    ''' @param mts
    ''' @param tp_name
    ''' @param ObjectBHSupport
    ''' @param ElementBHSupport
    ''' @returns
    Protected Overridable Function setupCondition(ByVal UniverseNameExtension As String, ByVal mts As MeasurementTypesTPIde, ByVal tp_name As String, _
                                           ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean) As Boolean
        Dim Count As Integer
        Dim addcondition As Boolean

        Try
            unvcondition = New BOCondition
            addcondition = False
            unvcondition.ClassName = databaseProxy.getValue(0).ToString()
            If unvcondition.ClassName.Length > 128 Then
                Trace.WriteLine("Universe Class '" & unvcondition.ClassName & "' for Condition '" & unvcondition.Name & "' exceeds maximum of 128 characters.")
            End If
            unvcondition.UniverseExtension = databaseProxy.getValue(1).ToString()
            unvcondition.Name = databaseProxy.getValue(2).ToString()
            If unvcondition.Name.Length > 128 Then
                Trace.WriteLine("Universe Condition '" & unvcondition.Name & "' exceeds maximum of 128 characters.")
            End If
            If databaseProxy.isDBNull(3) = False Then
                unvcondition.Description = Trim(databaseProxy.getString(3) + databaseProxy.getString(4) + databaseProxy.getString(5) + databaseProxy.getString(6))
            Else
                unvcondition.Description = ""
            End If
            If databaseProxy.isDBNull(7) = False Then
                condWhere = Trim(databaseProxy.getString(7) + databaseProxy.getString(8) + databaseProxy.getString(9) + databaseProxy.getString(10))
            Else
                condWhere = ""
            End If
            autoGenerate = databaseProxy.getValue(11).ToString()
            condObjClass = databaseProxy.getValue(12).ToString()
            condObject = databaseProxy.getValue(13).ToString()
            promptText = databaseProxy.getValue(14).ToString()
            multiSelection = databaseProxy.getValue(15).ToString()
            freeText = databaseProxy.getValue(16).ToString()
            unvcondition.ObjectBHRelated = databaseProxy.getValue(17).ToString()
            unvcondition.ElementBHRelated = databaseProxy.getValue(18).ToString()

            If unvcondition.UniverseExtension = "all" Then
                addcondition = True
            ElseIf unvcondition.UniverseExtension = "" AndAlso UniverseNameExtension = "" Then
                addcondition = True
            Else
                Dim UniverseCountList() As String
                Dim UnvCount As Integer
                If InStrRev(unvcondition.UniverseExtension, ",") = 0 Then
                    If unvcondition.UniverseExtension = UniverseNameExtension Then
                        addcondition = True
                    End If
                Else
                    UniverseCountList = Split(unvcondition.UniverseExtension, ",")
                    For UnvCount = 0 To UBound(UniverseCountList)
                        If UniverseCountList(UnvCount) = UniverseNameExtension Then
                            addcondition = True
                            Exit For
                        End If
                    Next
                End If
            End If
        Catch ex As Exception
            Trace.WriteLine("Error reading condition data from universe: " & ex.ToString())
        End Try

        Try
            If addcondition = True Then
                If unvcondition.ObjectBHRelated = ObjectBHSupport OrElse unvcondition.ElementBHRelated = ElementBHSupport OrElse (unvcondition.ObjectBHRelated = False AndAlso unvcondition.ElementBHRelated = False) Then
                    If InStrRev(unvcondition.Name, "(BHObject)") > 0 Then
                        For Count = 1 To mts.Count
                            If mts.Item(Count).RankTable = True Then
                                If (tp_name.StartsWith("DC_")) Then
                                    condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))
                                ElseIf (tp_name.StartsWith("PM_")) Then
                                    condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "PM_", ""))
                                ElseIf (tp_name.StartsWith("CM_")) Then
                                    condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "CM_", ""))
                                ElseIf (tp_name.StartsWith("CUSTOM_")) Then
                                    condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "CUSTOM_", ""))
                                Else
                                    condName = unvcondition.Name
                                End If

                                If mts.Item(Count).ElementBusyHours = True Then
                                    condName = Replace(condName, "(BHObject)", "Element")
                                End If
                                If mts.Item(Count).ObjectBusyHours <> "" Then
                                    condName = Replace(condName, "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                End If

                                Cls = universeProxy.getClass(unvcondition.ClassName)
                                If Cls Is Nothing Then
                                    Trace.WriteLine("Condition '" & condName & "' generation error: Class '" & unvcondition.ClassName & "' not found.")
                                    Return False
                                End If

                                Try
                                    Cond = universeProxy.getPredefinedCondition(Cls, condName)
                                    UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                                Catch e As Exception
                                    Cond = universeProxy.addPredefinedCondition(Cls, condName)
                                    UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                                    Trace.WriteLine("Added condition to universe: " & Cls.Name & "/" & Cond.Name)
                                End Try
                                If autoGenerate = "1" Then
                                    condWhere = "@Select(" & condObjClass & "\" & condObject & ")"
                                    condWhere &= setSelectMode(multiSelection)
                                    ' condWhere &= "@Prompt('" & promptText & ":','"
                                    condWhere &= getBHPromptText(mts.Item(Count), promptText)
                                    Try
                                        If mts.Item(Count).ElementBusyHours = True Then
                                            objName = Replace(condObject, "(BHObject)", "Element")
                                        End If
                                        If mts.Item(Count).ObjectBusyHours <> "" Then
                                            objName = Replace(condObject, "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                        End If
                                        Obj = Cls.Objects(objName)
                                    Catch e As Exception
                                        Trace.WriteLine("Condition '" & condName & "' generation error: Object '" & objName & "' not found in class '" & Cls.Name & "'.")
                                        Return False
                                    End Try
                                    condWhere &= getPromptType(Obj)
                                    condWhere &= "','" & condObjClass & "\" & condObject & "',"
                                    condWhere &= setListMode(multiSelection)
                                    condWhere &= setTextMode(freeText)
                                End If

                                If (tp_name.StartsWith("DC_")) Then
                                    condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "DC_", ""))
                                ElseIf (tp_name.StartsWith("PM_")) Then
                                    condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "PM_", ""))
                                ElseIf (tp_name.StartsWith("CM_")) Then
                                    condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "CM_", ""))
                                ElseIf (tp_name.StartsWith("CUSTOM_")) Then
                                    condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "CUSTOM_", ""))
                                Else
                                    condWhere = condWhere
                                End If

                                If mts.Item(Count).ElementBusyHours = True Then
                                    condWhere = Replace(condWhere, "(BHObject)", "Element")
                                End If
                                If mts.Item(Count).ObjectBusyHours <> "" Then
                                    condWhere = Replace(condWhere, "(BHObject)", mts.Item(Count).ObjectBusyHours)
                                End If

                                If condWhere <> Nothing And condWhere <> "" Then
                                    Cond.Where = condWhere
                                End If
                                If unvcondition.Description <> Nothing And unvcondition.Description <> "" Then
                                    Cond.Description = unvcondition.Description
                                End If
                                If ParseCondition(Cond, Cls) = False Then
                                    Trace.WriteLine("Error parsing condition")
                                    Return False
                                End If

                            End If
                        Next Count
                    ElseIf ((unvcondition.ObjectBHRelated = True AndAlso ObjectBHSupport = False) OrElse (unvcondition.ElementBHRelated = True AndAlso ElementBHSupport = False)) AndAlso (ObjectBHSupport = False AndAlso ElementBHSupport = False) Then
                        'Do nothing
                    Else
                        If (tp_name.StartsWith("DC_")) Then
                            condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "DC_", ""))
                        ElseIf (tp_name.StartsWith("PM_")) Then
                            condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "PM_", ""))
                        ElseIf (tp_name.StartsWith("CM_")) Then
                            condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "CM_", ""))
                        ElseIf (tp_name.StartsWith("CUSTOM_")) Then
                            condName = Replace(unvcondition.Name, "(TPNAME)", Replace(tp_name, "CUSTOM_", ""))
                        Else
                            condName = unvcondition.Name
                        End If

                        Cls = universeProxy.getClass(unvcondition.ClassName)
                        If Cls Is Nothing Then
                            Trace.WriteLine("Condition '" & condName & "' generation error: Class '" & unvcondition.ClassName & "' not found.")
                            Return False
                        Else
                            Try
                                Cond = universeProxy.getPredefinedCondition(Cls, condName)
                                UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                            Catch e As Exception
                                Cond = universeProxy.addPredefinedCondition(Cls, condName)
                                UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                                Trace.WriteLine("Added condition to universe: " & Cls.Name & "/" & Cond.Name)
                            End Try
                            If autoGenerate = "1" Then
                                condWhere = "@Select(" & condObjClass & "\" & condObject & ")"
                                condWhere &= setSelectMode(multiSelection)
                                condWhere &= "@Prompt('" & promptText & ":','"
                                Try
                                    Obj = Cls.Objects(condObject)
                                Catch e As Exception
                                    Trace.WriteLine("Condition '" & condName & "' generation error: Object '" & condObject & "' not found in class '" & Cls.Name & "'.")
                                    Return False
                                End Try
                                condWhere &= getPromptType(Obj)
                                condWhere &= "','" & condObjClass & "\" & condObject & "',"
                                condWhere &= setListMode(multiSelection)
                                condWhere &= setTextMode(freeText)
                            End If
                            If (tp_name.StartsWith("DC_")) Then
                                condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "DC_", ""))
                            ElseIf (tp_name.StartsWith("PM_")) Then
                                condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "PM_", ""))
                            ElseIf (tp_name.StartsWith("CM_")) Then
                                condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "CM_", ""))
                            ElseIf (tp_name.StartsWith("CUSTOM_")) Then
                                condWhere = Replace(condWhere, "(TPNAME)", Replace(tp_name, "CUSTOM_", ""))
                            End If

                            If condWhere <> Nothing And condWhere <> "" Then
                                Cond.Where = condWhere
                            End If

                            If unvcondition.Description <> Nothing And unvcondition.Description <> "" Then
                                Cond.Description = unvcondition.Description
                            End If
                            If ParseCondition(Cond, Cls) = False Then
                                Trace.WriteLine("Error parsing condition")
                                Return False
                            End If
                        End If
                    End If
                End If
            End If
        Catch ex As Exception
            Trace.WriteLine("Error adding condition to universe: " & ex.ToString())
        End Try

        Return True
    End Function

    '' Gets the prompt text for a busy hour condition.    
    ''@param    measType            This is the measurement type for the busy hour (the rank measurement type)
    ''@param    defaultPromptText   E.g. "Busy Hour Type". This will be changed to include the busy hour name at the beginning.
    ''@returns  The @Prompt string that will be used in the condition.
    Public Function getBHPromptText(ByVal measType As MeasurementTypesTPIde.MeasurementType, ByVal defaultPromptText As String) As String
        Dim newPromptText As String = ""
        If measType.ElementBusyHours = True Then
            newPromptText = "Element " & defaultPromptText
        ElseIf measType.ObjectBusyHours <> "" Then
            newPromptText = measType.ObjectBusyHours & " " & defaultPromptText
        Else
            newPromptText = defaultPromptText
        End If

        Return "@Prompt('" & newPromptText & ":','"
    End Function

    ''
    'Adds Busy Hour conditions to the universe 
    '@param    Univ    Reference to the universe
    '@param    mts     The measurement types
    '@return   Boolean True if adding the busy hour conditions was successful.
    Public Function addBusyHourConditions(ByRef mts As MeasurementTypesTPIde) As Boolean
        Dim success As Boolean
        success = True
        Dim promptText As String = ""

        Try
            'Get a list of the rank tables
            Dim rankTableList As New ArrayList()
            Dim tpUtilities As ITPUtilitiesTPIde = createTPUtilities()
            rankTableList = tpUtilities.getRankMeasurementTypes(mts)
            Dim rankMT As MeasurementTypesTPIde.MeasurementType
            'Go through the rank tables, and add a condition for each one
            Dim Count As Integer
            For Count = 0 To (rankTableList.Count - 1)
                rankMT = rankTableList.Item(Count)
                Dim listOfKeys As ArrayList = getListOfElementKeys(rankMT)

                Dim rankClassName As String
                rankClassName = rankMT.TypeName & "_RANKBH"
                ' Add conditions for Busy Hour Type, Busy Hour Object and Date:
                promptText = getBHPromptText(rankMT, "Busy Hour Type")
                If (addCondition(rankClassName, "Busy Hour Type", "Select Busy Hour Type", "0", "0", "1", promptText) = False) Then
                    Trace.WriteLine("Error adding condition: " & rankClassName & "\" & "Select Busy Hour Type")
                End If

                promptText = getBHPromptText(rankMT, "Busy Hour Object")
                If (addCondition(rankClassName, "Busy Hour Object", "Select Busy Hour Object", "0", "0", "1", promptText) = False) Then
                    Trace.WriteLine("Error adding condition: " & rankClassName & "\" & "Select Busy Hour Object")
                End If

                promptText = getBHPromptText(rankMT, "Date (busy hour)")
                If (addCondition(rankClassName, "Date", "Select Date", "0", "0", "1", promptText) = False) Then
                    Trace.WriteLine("Error adding condition: " & rankClassName & "\" & "Select Date")
                End If

                For Each elementkey As String In listOfKeys
                    promptText = getBHPromptText(rankMT, elementkey & " (busy hour)")
                    If (addCondition(rankClassName, elementkey, "Select " & elementkey, "0", "0", "1", promptText) = False) Then
                        Trace.WriteLine("Error adding condition: " & rankClassName & "\" & "Select " & elementkey)
                    End If
                Next
            Next
        Catch ex As Exception
            Trace.WriteLine("Error adding busy hour conditions: " & ex.ToString())
            Console.WriteLine("Error adding busy hour conditions: " & ex.ToString())
            success = False
        End Try
        Return success
    End Function

    ''
    'Gets list of element keys for a measurement type
    '@param     rankMT      The rank measurement type.
    '@returns   listOfKeys  An array list with the names of the element keys.  
    Private Function getListOfElementKeys(ByVal rankMT As MeasurementTypesTPIde.MeasurementType) As ArrayList
        Dim listOfKeys As ArrayList
        listOfKeys = New ArrayList()
        ' Get element keys:
        Dim keys As CounterKeysTPIde
        Dim key As CounterKeysTPIde.CounterKey
        keys = rankMT.CounterKeys
        Dim keyCount As Integer
        keyCount = 1

        For keyCount = 1 To keys.Count
            key = keys.Item(keyCount)
            If (key.Element = 1) Then
                listOfKeys.Add(key.CounterKeyName)
            End If
        Next
        Return listOfKeys
    End Function

    Public Function ParseCondition(ByRef Cond As Designer.PredefinedCondition, ByRef Cls As Designer.IClass) As Boolean
        Dim Result As MsgBoxResult
        If ConditionParse = True Then
            Try
                Cond.Parse()
            Catch ex As Exception
                Trace.WriteLine("Condition Parse failed for '" & Cls.Name & "/" & Cond.Name & "' with Where clause '" & Cond.Where & "'.")
                Trace.WriteLine("Condition Parse Exception: " & ex.ToString)
            End Try
        End If
        Return True
    End Function

    ''
    'Helper function for addCondition. Calls addCondition() with default values.
    '@param Univ            Reference to the universe.
    '@param univ_class      String holding the class name
    '@param univ_object     String holding the object name
    '@param description     Condition description
    '@returns               True if the condition was added ok and parsing was successful.
    '@remarks Overloaded function for add condition. Sets default values for select mode, list mode and text mode in @Prompt statement.
    Public Function addCondition(ByRef univ_class As String, ByRef univ_object As String, ByRef description As String) As Boolean
        Dim addedCondition As Boolean = True
        Dim promptText As String = "@Prompt('" & univ_object & ":','"
        addedCondition = addCondition(univ_class, univ_object, description, "1", "1", "1", promptText)
        If (addedCondition = False) Then
            Trace.WriteLine("Failed to add condition for : " & univ_class + "\" & univ_object)
        End If
        Return addedCondition
    End Function

    ''
    'Adds a condition to the universe.
    '@param Univ            Reference to the universe.
    '@param univ_class      String holding the class name
    '@param univ_object     String holding the object name
    '@param description     Condition description
    '@param selectMode      Gets either "=" or "IN" for @Select statement.
    '@param listMode        Gets either "Multi" or "Mono" for @Prompt statement.
    '@param textMode        Gets either "free" or "Constrained" for @Prompt statement.
    '@param promptText      The text to use for the condition's prompt.
    '@returns               True if the condition was added ok and parsing was successful.
    Protected Function addCondition(ByVal univ_class As String, ByVal univ_object As String, ByVal description As String, _
                                 ByVal selectMode As String, ByVal listMode As String, ByVal textMode As String, ByVal promptText As String) As Boolean
        Dim Cond As Designer.PredefinedCondition
        Dim Obj As Designer.IObject
        Dim Cls As Designer.IClass

        Dim condName As String
        Dim condWhere As String

        condName = "Select " & univ_object

        Cls = universeProxy.getClass(univ_class)
        If Cls Is Nothing Then
            Trace.WriteLine("Condition '" & condName & "' generation error: Class '" & univ_class & "' not found.")
            Return False
        End If

        Try
            Cond = universeProxy.getPredefinedCondition(Cls, condName)
            UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
        Catch e As Exception
            Cond = universeProxy.addPredefinedCondition(Cls, condName)
            UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
        End Try
        condWhere = "@Select(" & univ_class & "\" & univ_object & ")"
        condWhere &= setSelectMode(selectMode)
        condWhere &= promptText
        Try
            Obj = universeProxy.getObject(Cls, univ_object)
        Catch e As Exception
            Trace.WriteLine("Condition '" & condName & "' generation error: Object '" & univ_object & "' not found in class '" & univ_class & "'.")
            Return False
        End Try

        condWhere &= getPromptType(Obj)
        condWhere &= "','" & univ_class & "\" & univ_object & "',"
        condWhere &= setListMode(listMode)
        condWhere &= setTextMode(textMode)
        Cond.Description = description
        Cond.Where = condWhere
        If ParseCondition(Cond, Cls) = False Then
            Return False
        End If
        Return True
    End Function

    Function setTextMode(ByRef Setting As String) As String
        If LCase(Setting) = "1" Then
            Return "free)"
        Else
            Return "CONSTRAINED)"
        End If
    End Function
    Function setListMode(ByRef Setting As String) As String
        If LCase(Setting) = "1" Then
            Return "multi,"
        Else
            Return "mono,"
        End If
    End Function
    Function setSelectMode(ByRef Setting As String) As String
        If LCase(Setting) = "1" Then
            Return " IN "
        Else
            Return " = "
        End If
    End Function

    ''
    'Gets the prompt type for an object.
    '@param     Obj         Reference to the object
    '@returns   promptType  A string that will be used in the @Prompt part of a condition statement.
    '@remarks   @Prompt takes an argument like "A" for the type of the object.
    Public Function getPromptType(ByRef Obj As Designer.IObject) As String
        ' Prompt type is "A" by default
        Dim promptType As String = "A"
        If (Obj Is Nothing) Then
            promptType = "A"
        ElseIf Obj.Type = Designer.DsObjectType.dsDateObject Then
            promptType = "D"
        ElseIf Obj.Type = Designer.DsObjectType.dsCharacterObject Then
            promptType = "A"
        ElseIf Obj.Type = Designer.DsObjectType.dsNumericObject Then
            promptType = "N"
        Else
            promptType = "A"
        End If
        Return promptType
    End Function
End Class
