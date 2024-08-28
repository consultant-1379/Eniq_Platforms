Option Strict Off

''
'  BOContexts class is a collection of BOJoin classes
'
Public Class BOContexts
    Private _contexts As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of BOJoin classes in BOContexts class
    '
    ' @param Index Specifies the index in the BOJoin class
    ' @return Count of BOJoin classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _contexts Is Nothing) Then
                Return _contexts.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets BOJoin class from BOContexts class based on given index.
    '
    ' @param Index Specifies the index in the BOContexts class
    ' @return Reference to BOJoin
    Public ReadOnly Property Item(ByVal Index As Integer) As BOJoin
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_contexts.Item(Index - 1), BOJoin)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds BOJoin class to BOContexts class
    '
    ' @param ValueIn Specifies reference to BOJoin
    Public Sub AddItem(ByVal ValueIn As BOJoin)

        If (Not _contexts Is Nothing) Then
            _contexts.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOJoin defines class context's joins.
    '
    Public Class BOJoin
        Private m_Context As String
        Private m_FirstTable As String
        Private m_SecondTable As String
        Private m_JoinLevel As String

        ''
        ' Gets and sets value for Context parameter. 
        ' Context defines context's name.
        '
        ' @param Value Specifies value of Context parameter
        ' @return Value of Context parameter
        Public Property Context()
            Get
                Context = m_Context
            End Get

            Set(ByVal Value)
                m_Context = Value
            End Set

        End Property
        ''
        ' Gets and sets value for FirstTable parameter. 
        ' FirstTable defines join's first table.
        '
        ' @param Value Specifies value of FirstTable parameter
        ' @return Value of FirstTable parameter
        Public Property FirstTable()
            Get
                FirstTable = m_FirstTable
            End Get

            Set(ByVal Value)
                m_FirstTable = Value
            End Set

        End Property

        ''
        ' Gets and sets value for SecondTable parameter. 
        ' SecondTable defines join's second table.
        '
        ' @param Value Specifies value of SecondTable parameter
        ' @return Value of SecondTable parameter
        Public Property SecondTable()
            Get
                SecondTable = m_SecondTable
            End Get

            Set(ByVal Value)
                m_SecondTable = Value
            End Set

        End Property
        ''
        ' Gets and sets value for JoinLevel parameter. 
        ' JoinLevel defines join's level.
        '
        ' @param Value Specifies value of JoinLevel parameter
        ' @return Value of JoinLevel parameter
        Public Property JoinLevel()
            Get
                JoinLevel = m_JoinLevel
            End Get

            Set(ByVal Value)
                m_JoinLevel = Value
            End Set

        End Property

    End Class
    ''
    ' Adds one context to universe. If context already exists, it is selected. 
    '
    ' @param Univ Specifies reference to universe
    ' @param ContextName Name of the context
    ' @return Reference to context

    Function addContext(ByRef Univ As Object, ByRef ContextName As String) As Designer.Context

        Dim Conxt As Designer.Context
        Try
            Conxt = Univ.Contexts.Item(ContextName)
            UniverseFunctions.updatedContexts &= Conxt.Name & ";"
        Catch ex As Exception
            Conxt = Univ.Contexts.Add(ContextName)
            UniverseFunctions.updatedContexts &= Conxt.Name & ";"
        Finally
            'Do nothing
        End Try
        Return Conxt
    End Function
End Class

