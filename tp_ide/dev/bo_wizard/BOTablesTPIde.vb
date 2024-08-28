Option Strict Off
''
'  BOTables class is a collection of BOTable classes
'
Public Class BOTablesTPIde
    Private _tables As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of BOTable classes in BOTables class
    '
    ' @param Index Specifies the index in the BOTables class
    ' @return Count of BOTable classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _tables Is Nothing) Then
                Return _tables.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOTable class from BOTables class based on given index.
    '
    ' @param Index Specifies the index in the BOTables class
    ' @return Reference to BOTable
    Public ReadOnly Property Item(ByVal Index As Integer) As BOTable
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_tables.Item(Index - 1), BOTable)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BOTable class to BOTables class
    '
    ' @param ValueIn Specifies reference to BOTable
    Public Sub AddItem(ByVal ValueIn As BOTable)

        If (Not _tables Is Nothing) Then
            _tables.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOTable class defines tables for universe.
    '
    Public Class BOTable
        Private m_Owner As String
        Private m_Name As String
        Private m_AliasName As String
        Private m_ElementBHRelated As Boolean
        Private m_ObjectBHRelated As Boolean

        Public Property Owner()
            Get
                Owner = m_Owner
            End Get

            Set(ByVal Value)
                m_Owner = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Name parameter. 
        ' Name defines name of table.
        '
        ' @param Value Specifies value of Name parameter
        ' @return Value of Name parameter
        Public Property Name()
            Get
                Name = m_Name
            End Get

            Set(ByVal Value)
                m_Name = Value
            End Set

        End Property

        ''
        ' Gets and sets value for AliasName parameter. 
        ' AliasName defines name of alias of table.
        '
        ' @param Value Specifies value of AliasName parameter
        ' @return Value of AliasName parameter
        Public Property AliasName()
            Get
                AliasName = m_AliasName
            End Get

            Set(ByVal Value)
                m_AliasName = Value
            End Set

        End Property

        Public Property ElementBHRelated()
            Get
                ElementBHRelated = m_ElementBHRelated
            End Get

            Set(ByVal Value)
                If LCase(Value) = "1" Then
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
                If LCase(Value) = "1" Then
                    m_ObjectBHRelated = True
                Else
                    m_ObjectBHRelated = False
                End If
            End Set

        End Property

    End Class
    ''
    ' Adds extra tables to universe. 
    '
    ' @param Univ Specifies reference to universe
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @remarks Tables are defined in TP definition's sheet 'Universe tables'.

    Public Function addTables(ByRef Univ As Designer.Universe, ByRef conn As System.Data.Odbc.OdbcConnection, _
                              ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                              ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef NewUniverse As Boolean, _
                              ByRef UniverseNameExtension As String, ByRef TechPackTPIde As String) As Boolean

        Dim Tbl As Designer.Table
        Dim AliasTbl As Designer.Table
        Dim botable As BOTable
        Dim result As MsgBoxResult
        Dim Aliases() As String
        Dim count As Integer
        Dim UnvExtension As String
        Dim addTable As Boolean

        Dim unvtable As String
        unvtable = "SELECT OWNER,TABLENAME,UNIVERSEEXTENSION,ALIAS,OBJ_BH_REL,ELEM_BH_REL FROM Universetable WHERE VERSIONID='" & TechPackTPIde & "'"

        dbCommand = New System.Data.Odbc.OdbcCommand(unvtable, conn)

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
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Try
                    botable = New BOTable
                    addTable = False
                    botable.Owner = dbReader.GetValue(0).ToString()
                    botable.Name = dbReader.GetValue(1).ToString()
                    UnvExtension = LCase(dbReader.GetValue(2).ToString())
                    botable.AliasName = dbReader.GetValue(3).ToString()
                    botable.ObjectBHRelated = dbReader.GetValue(4).ToString()
                    botable.ElementBHRelated = dbReader.GetValue(5).ToString()

                    If UnvExtension = "all" Then
                        addTable = True
                    ElseIf UnvExtension = "" AndAlso UniverseNameExtension = "" Then
                        addTable = True
                    Else
                        Dim UniverseCountList() As String
                        Dim UnvCount As Integer
                        If InStrRev(UnvExtension, ",") = 0 Then
                            If UnvExtension = UniverseNameExtension Then
                                addTable = True
                            End If
                        Else
                            UniverseCountList = Split(UnvExtension, ",")
                            For UnvCount = 0 To UBound(UniverseCountList)
                                If UniverseCountList(UnvCount) = UniverseNameExtension Then
                                    addTable = True
                                    Exit For
                                End If
                            Next
                        End If
                    End If
                Catch ex As Exception
                    Trace.WriteLine("Error reading table information from database: " & ex.ToString())
                End Try

                Try
                    If addTable = True Then
                        If (botable.ObjectBHRelated = ObjectBHSupport OrElse botable.ElementBHRelated = ElementBHSupport) OrElse (botable.ObjectBHRelated = False AndAlso botable.ElementBHRelated = False) Then
                            Try
                                Tbl = Univ.Tables.Item(botable.Owner & "." & botable.Name)
                                If NewUniverse = True Then
                                    Trace.WriteLine("Table '" & botable.Owner & "." & botable.Name & "' already defined in universe.")
                                End If
                                UniverseFunctionsTPIde.updatedTables &= Tbl.Name & ";"
                                Trace.WriteLine("Table '" & botable.Owner & "." & botable.Name & "' already in the universe " & Univ.LongName)
                            Catch e As Exception
                                Tbl = Univ.Tables.Add(botable.Owner & "." & botable.Name)
                                UniverseFunctionsTPIde.updatedTables &= Tbl.Name & ";"
                                Trace.WriteLine("Added table '" & botable.Owner & "." & botable.Name & "' to universe " & Univ.LongName)
                            End Try
                            If botable.AliasName <> "" Then
                                Aliases = Split(botable.AliasName, ",")
                                For count = 0 To UBound(Aliases)
                                    Try
                                        AliasTbl = Univ.Tables.Item(Aliases(count))
                                        If NewUniverse = True Then
                                            Trace.WriteLine("Alias '" & Aliases(count) & "' already defined in universe.")
                                        End If
                                        UniverseFunctionsTPIde.updatedTables &= Aliases(count) & ";"
                                        Trace.WriteLine("Table alias '" & Aliases(count) & "' already in the universe " & Univ.LongName)
                                    Catch e As Exception
                                        AliasTbl = Tbl.CreateAlias(Aliases(count))
                                        UniverseFunctionsTPIde.updatedTables &= Aliases(count) & ";"
                                        Trace.WriteLine("Added table alias '" & Aliases(count) & "' to universe " & Univ.LongName)
                                    End Try
                                Next count
                            End If
                        End If
                    End If
                Catch ex As Exception
                    Trace.WriteLine("Error adding table to universe: " & ex.ToString())
                End Try

            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()
        Return True

    End Function
    ''
    ' Adds one table to universe. If table already exists, it is selected. 
    '
    ' @param Univ Specifies reference to universe
    ' @param TableName Name of the table
    ' @return Reference to table
    ' @remarks Owner 'DC' is Added automatically

    Function addTable(ByRef Univ As Designer.Universe, ByRef TableName As String, ByRef NewUniverse As Boolean) As Boolean
        Dim TableOwner As String
        Dim result As MsgBoxResult

        TableOwner = "DC"
        Dim Tbl As Designer.Table
        Try
            Tbl = Univ.Tables.Item(TableOwner & "." & TableName)
            If NewUniverse = True Then
                Trace.WriteLine("Table '" & TableOwner & "." & TableName & "' already defined in universe.")
            End If
            UniverseFunctionsTPIde.updatedTables &= Tbl.Name & ";"
        Catch e As Exception
            Try
                Tbl = Univ.Tables.Add(TableOwner & "." & TableName)
                Trace.WriteLine("Added table '" & TableOwner & "." & TableName & " to universe: " & Univ.LongName)
                UniverseFunctionsTPIde.updatedTables &= Tbl.Name & ";"
            Catch ex As Exception
                Trace.WriteLine("Table '" & TableOwner & "." & TableName & "' adding failed. Exception" & ex.Message)
            End Try
        End Try

        Return True
    End Function
End Class
