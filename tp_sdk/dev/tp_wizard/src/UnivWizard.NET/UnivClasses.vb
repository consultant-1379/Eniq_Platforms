Option Strict Off

''
'  UnivClasses class is a collection of UnivClass classes
'
Public Class UnivClasses
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
                If LCase(Value) = "x" Then
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
                If LCase(Value) = "x" Then
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
    Public Function getClasses(ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef logs As logMessages) As Boolean

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Universe classes$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(1).ToString() = "" Then
                Exit While
            Else
                Dim univ_cls = New UnivClass
                univ_cls.ParentClassName = dbReader.GetValue(0).ToString()
                If univ_cls.ParentClassName.Length() > 35 Then
                    logs.AddLogText("Universe Parent Class '" & univ_cls.ParentClassName & "' for Universe Class '" & univ_cls.ClassName & "' exceeds maximum of 35 characters.")
                    Return False
                End If
                univ_cls.ClassName = dbReader.GetValue(1).ToString()
                If univ_cls.ClassName.Length() > 35 Then
                    logs.AddLogText("Universe Class '" & univ_cls.ClassName & "' exceeds maximum of 35 characters.")
                    Return False
                End If
                univ_cls.Description = dbReader.GetValue(2).ToString()

                If dbReader.FieldCount < 4 Then
                    univ_cls.ObjectBHRelated = False
                Else
                    univ_cls.ObjectBHRelated = dbReader.GetValue(3).ToString()
                End If
                If dbReader.FieldCount < 5 Then
                    univ_cls.ElementBHRelated = False
                Else
                    univ_cls.ElementBHRelated = dbReader.GetValue(4).ToString()
                End If

                AddItem(univ_cls)
            End If
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
    Function addRootClass(ByRef Univ As Object, ByRef univ_class As UnivClass, ByRef NewUniverse As Boolean, ByRef logs As logMessages) As Boolean
        Dim Cls As Designer.Class
        Dim result As MsgBoxResult

        Try
            Cls = Univ.Classes.FindClass(univ_class.ClassName)
            If NewUniverse = True Then
                logs.AddLogText("Class '" & univ_class.ClassName & "' already defined in universe.")
            End If
            UniverseFunctions.updatedClasses &= Cls.Name & ";"
        Catch e As Exception
            Cls = Univ.Classes.Add(univ_class.ClassName)
            UniverseFunctions.updatedClasses &= Cls.Name & ";"
        Finally
            Cls.Description = univ_class.Description
            If univ_class.Hidden = True Then
                Cls.Show = False
            End If
        End Try

        Return True
    End Function

    ''
    ' Adds one class to universe under classes' parent. If class already exists, it is selected. 
    '
    ' @param Univ Specifies reference to universe
    ' @param univ_class Specifies reference to universe class
    ' @return Reference to class
    Function addChildClass(ByRef Univ As Object, ByRef univ_class As UnivClass, ByRef NewUniverse As Boolean, ByRef logs As logMessages) As Boolean
        Dim Cls As Designer.Class
        Dim ParentCls As Designer.Class
        Dim result As MsgBoxResult

        Try
            ParentCls = Univ.Classes.FindClass(univ_class.ParentClassName)
        Catch ex As Exception
            logs.AddLogText("Class '" & univ_class.ParentClassName & "' not found in universe.")
            Return True
        End Try

        Try
            Cls = Univ.Classes.FindClass(univ_class.ClassName)
            If NewUniverse = True Then
                logs.AddLogText("Class '" & univ_class.ClassName & "' already defined in universe.")
            End If
            UniverseFunctions.updatedClasses &= Cls.Name & ";"
        Catch e As Exception
            Cls = ParentCls.Classes.Add(univ_class.ClassName)
            UniverseFunctions.updatedClasses &= Cls.Name & ";"
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
            UniverseFunctions.updatedClasses &= Cls.Name & ";"
        Catch e As Exception
            Cls = ParentCls.Classes.Add(ClassName)
            UniverseFunctions.updatedClasses &= Cls.Name & ";"
        Finally
            Cls.Description = Description
        End Try

        Return Cls
    End Function
End Class
