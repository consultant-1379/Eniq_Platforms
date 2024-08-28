Option Strict Off

Imports System.Collections

''
'  UnivClasses class is a collection of UnivClass classes
'
Public Class UnivClassesTPIde
    Private _classes As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of UnivClass classes in UnivClasses class
    '
    ' @param Index Specifies the index in the UnivClasses class
    ' @return Count of UnivClass classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _classes Is Nothing) Then
                Return _classes.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets UnivClass class from UnivClasses class based on given index.
    '
    ' @param Index Specifies the index in the UnivClasses class
    ' @return Reference to UnivClass
    Public ReadOnly Property Item(ByVal Index As Integer) As UnivClass
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_classes.Item(Index - 1), UnivClass)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds UnivClass class to UnivClasses class
    '
    ' @param ValueIn Specifies reference to UnivClass
    Public Sub AddItem(ByVal ValueIn As UnivClass)

        If (Not _classes Is Nothing) Then
            _classes.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOObject defines universe's classes.
    '
    Public Class UnivClass
        Private m_ClassName As String
        Private m_OldClassName As String
        Private m_Hidden As Boolean
        Private m_Description As String
        Private m_ParentClassName As String
        Private m_OldParentClassName As String
        Private m_ElementBHRelated As Boolean
        Private m_ObjectBHRelated As Boolean
        Private m_UniverseExtension As String

        Public Property UniverseExtension()
            Get
                UniverseExtension = m_UniverseExtension
            End Get

            Set(ByVal Value)
                m_UniverseExtension = Value
            End Set

        End Property
        ''
        ' Gets and sets value for ClassName parameter. 
        ' ClassName defines class name.
        '
        ' @param Value Specifies value of ClassName parameter
        ' @return Value of ClassName parameter
        Public Property ClassName()
            Get
                ClassName = m_ClassName
            End Get

            Set(ByVal Value)
                m_ClassName = Value
            End Set

        End Property

        ''
        ' Gets and sets value for OldClassName parameter. 
        ' OldClassName defines previous class name.
        '
        ' @param Value Specifies value of OldClassName parameter
        ' @return Value of OldClassName parameter
        Public Property OldClassName()
            Get
                OldClassName = m_OldClassName
            End Get

            Set(ByVal Value)
                m_OldClassName = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Hidden parameter. 
        ' Hidden defines whether class is visible.
        '
        ' @param Value Specifies value of Hidden parameter
        ' @return Value of Hidden parameter
        Public Property Hidden()
            Get
                Hidden = m_Hidden
            End Get

            Set(ByVal Value)
                m_Hidden = Value
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
                m_Description = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ParentClassName parameter. 
        ' ParentClassName defines parent class name.
        '
        ' @param Value Specifies value of ParentClassName parameter
        ' @return Value of ParentClassName parameter
        Public Property ParentClassName()
            Get
                ParentClassName = m_ParentClassName
            End Get

            Set(ByVal Value)
                m_ParentClassName = Value
            End Set

        End Property

        ''
        ' Gets and sets value for OldParentClassName parameter. 
        ' OldParentClassName defines previous parent class name.
        '
        ' @param Value Specifies value of OldParentClassName parameter
        ' @return Value of OldParentClassName parameter
        Public Property OldParentClassName()
            Get
                OldParentClassName = m_OldParentClassName
            End Get

            Set(ByVal Value)
                m_OldParentClassName = Value
            End Set

        End Property

        Public Property ElementBHRelated()
            Get
                ElementBHRelated = m_ElementBHRelated
            End Get

            Set(ByVal Value)
                If LCase(Value) = "1" Then 'JTS 15.9.2008
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
                If LCase(Value) = "1" Then 'JTS 15.9.2008
                    m_ObjectBHRelated = True
                Else
                    m_ObjectBHRelated = False
                End If
            End Set

        End Property

    End Class

    ''
    ' Gets classes defined in TP definition. 
    '
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @remarks Objects are defined in TP definition's sheet 'Universe classes'.
    Public Function getClasses(ByRef conn As System.Data.Odbc.OdbcConnection, ByRef dbCommand As System.Data.Odbc.OdbcCommand, _
                               ByRef dbReader As System.Data.Odbc.OdbcDataReader, ByRef TechPackTPIde As String) As Boolean

        Dim univclasssql As String
        univclasssql = "SELECT CLASSNAME,UNIVERSEEXTENSION," & _
        "SUBSTR(DESCRIPTION,1,8000),SUBSTR(DESCRIPTION,8001,8000),SUBSTR(DESCRIPTION,16001,8000),SUBSTR(DESCRIPTION,24001,8000)" & _
        ",PARENT,OBJ_BH_REL,ELEM_BH_REL FROM Universeclass where versionid ='" & TechPackTPIde & "'and classname not like ('%_Keys') ORDER BY ORDERNRO"
        '",PARENT,OBJ_BH_REL,ELEM_BH_REL FROM Universeclass where versionid ='" & TechPackTPIde & "'"
        'and classname not like ('%_Keys')" JTS


        dbCommand = New System.Data.Odbc.OdbcCommand(univclasssql, conn)

        Try
            If dbReader.IsClosed = False Then
                dbReader.Close()
            End If
            dbReader = dbCommand.ExecuteReader()
        Catch ex As Exception
            Trace.WriteLine("Database Exception: " & ex.ToString)
            Return False
        End Try

        While (dbReader.Read())
            Try
                Dim univ_cls = New UnivClass
                univ_cls.ClassName = dbReader.GetValue(0).ToString()
                If univ_cls.ClassName.Length() > 128 Then
                    Trace.WriteLine("Universe Class '" & univ_cls.ClassName & "' exceeds maximum of 128 characters.")
                    Return False
                End If
                univ_cls.UniverseExtension = LCase(dbReader.GetValue(1).ToString())
                univ_cls.Description = Trim(dbReader.GetString(2) + dbReader.GetString(3) + dbReader.GetString(4) + dbReader.GetString(5))
                univ_cls.ParentClassName = dbReader.GetValue(6).ToString()
                If univ_cls.ParentClassName.Length() > 128 Then
                    Trace.WriteLine("Universe Parent Class '" & univ_cls.ParentClassName & "' for Universe Class '" & univ_cls.ClassName & "' exceeds maximum of 128 characters.")
                    Return False
                End If
                univ_cls.ObjectBHRelated = dbReader.GetValue(7).ToString()
                univ_cls.ElementBHRelated = dbReader.GetValue(8).ToString()
                AddItem(univ_cls)
            Catch ex As Exception
                Trace.WriteLine("Error reading universe class information from database: " & ex.ToString())
            End Try
        End While
        dbReader.Close()
        dbCommand.Dispose()

        Return True

    End Function

    ''
    ' Adds one class to universe directly under root. If class already exists, it is selected. 
    '
    ' @param Univ Specifies reference to universe
    ' @param univ_class Specifies reference to universe class
    ' @return Reference to class
    Function addRootClass(ByRef Univ As Object, ByRef univ_class As UnivClass, ByRef NewUniverse As Boolean) As Boolean
        Dim Cls As Designer.Class
        Dim result As MsgBoxResult

        Try
            Cls = Univ.Classes.FindClass(univ_class.ClassName)
            If NewUniverse = True Then
                Trace.WriteLine("Root class '" & univ_class.ClassName & "' already defined in universe.")
            End If
            UniverseFunctionsTPIde.updatedClasses &= Cls.Name & ";"
        Catch e As Exception
            Cls = Univ.Classes.Add(univ_class.ClassName)
            UniverseFunctionsTPIde.updatedClasses &= Cls.Name & ";"
            Trace.WriteLine("Added root class '" & univ_class.ClassName & "'.")
        Finally
            Cls.Description = univ_class.Description
            If univ_class.Hidden = True Then
                Cls.Show = False
            End If
        End Try

        Return True
    End Function

    ''
    ' Adds rank classes for the busy hours.
    '@param mts
    '@returns
    Public Function getRankingBusyHourClasses(ByVal mts As MeasurementTypesTPIde) As Boolean
        Dim success As Boolean
        success = True ' True by default

        Dim tpUtilities As New TPUtilitiesTPIde
        Dim parentClassName As String = "Busy Hour"

        'Get a list of the rank tables
        Dim rankTableList As New ArrayList()
        rankTableList = tpUtilities.getRankMeasurementTypes(mts)
        Dim unvMt As MeasurementTypesTPIde.MeasurementType

        'Go through the rank tables, and add a class for each one
        Dim Count As Integer
        For Count = 0 To (rankTableList.Count - 1)
            unvMt = rankTableList.Item(Count)
            Dim className As String = unvMt.TypeName & "_RANKBH"

            ' Check class name length
            If checkClassNameLength(className) = False Then
                success = False
                Exit For
            End If

            ' Check parent class name length
            If checkClassNameLength(parentClassName) = False Then
                success = False
                Exit For
            End If

            Dim univ_cls As UnivClass = New UnivClass()
            univ_cls.ClassName = className 'className is unvMt.TypeName
            univ_cls.UniverseExtension = unvMt.ExtendedUniverse
            univ_cls.Description = className & " Busy Hour objects"
            univ_cls.ParentClassName = parentClassName
            univ_cls.ObjectBHRelated = False
            univ_cls.ElementBHRelated = False
            AddItem(univ_cls)
        Next
        Return success
    End Function

    ''
    '
    '@param className
    '@returns
    Private Function checkClassNameLength(ByVal className As String) As Boolean
        Dim success As Boolean
        success = True
        If className.Length() > 128 Then
            Trace.WriteLine("Universe Class '" & className & "' exceeds maximum of 128 characters.")
            Return False
        End If
        Return success
    End Function

    ''
    ' Adds one class to universe under classes' parent. If class already exists, it is selected. 
    '
    ' @param Univ Specifies reference to universe
    ' @param univ_class Specifies reference to universe class
    ' @return Reference to class
    Function addChildClass(ByRef Univ As Object, ByRef univ_class As UnivClass, ByRef NewUniverse As Boolean) As Boolean
        Dim Cls As Designer.Class
        Dim ParentCls As Designer.Class
        Dim result As MsgBoxResult

        Try
            ParentCls = Univ.Classes.FindClass(univ_class.ParentClassName)
        Catch ex As Exception
            Trace.WriteLine("Class '" & univ_class.ParentClassName & "' not found in universe.")
            Return True
        End Try

        Try
            Cls = Univ.Classes.FindClass(univ_class.ClassName)
            If NewUniverse = True Then
                Trace.WriteLine("Class '" & univ_class.ClassName & "' already defined in universe.")
            End If
            UniverseFunctionsTPIde.updatedClasses &= Cls.Name & ";"
        Catch e As Exception
            Cls = ParentCls.Classes.Add(univ_class.ClassName)
            UniverseFunctionsTPIde.updatedClasses &= Cls.Name & ";"
            Trace.WriteLine("Added child class '" & univ_class.ClassName & "'.")
        Finally
            Cls.Description = univ_class.Description
            If univ_class.Hidden = True Then
                Cls.Show = False
            End If
        End Try

        Return True
    End Function

    ''
    ' Adds one class to universe under given class. If class already exists, it is selected. 
    '
    ' @param Univ Specifies reference to universe
    ' @param ParentCls Specifies reference to parent class
    ' @param ClassName Specifies name of class
    ' @param Description Specifies description of class
    ' @return Reference to class
    Function addClass(ByRef Univ As Object, ByRef ParentCls As Designer.Class, ByRef ClassName As String, ByRef Description As String) As Designer.Class
        Dim Cls As Designer.Class

        Try
            Cls = ParentCls.Classes.FindClass(ClassName)
            UniverseFunctionsTPIde.updatedClasses &= Cls.Name & ";"
        Catch e As Exception
            Cls = ParentCls.Classes.Add(ClassName)
            UniverseFunctionsTPIde.updatedClasses &= Cls.Name & ";"
        Finally
            Cls.Description = Description
        End Try

        Return Cls
    End Function
End Class
