Option Strict Off

''
'  DataItems class is a collection of DataItem classes
'
Public NotInheritable Class DataItems
    Private _dataitems As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    ' Gets count of DataItem classes in DataItems class
    '
    ' @param Index Specifies the index in the DataItems class
    ' @return Count of DataItem classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _dataitems Is Nothing) Then
                Return _dataitems.Count
            End If
            Return 0
        End Get
    End Property

    ''
    ' Gets DataItem class from DataItems class based on given index.
    '
    ' @param Index Specifies the index in the DataItems class
    ' @return Reference to DataItem
    Public ReadOnly Property Item(ByVal Index As Integer) As DataItem
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_dataitems.Item(Index - 1), DataItem)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds DataItem class to DataItems class
    '
    ' @param ValueIn Specifies reference to DataItem
    Public Sub AddItem(ByVal ValueIn As DataItem)

        If (Not _dataitems Is Nothing) Then
            _dataitems.Add(ValueIn)
        End If

    End Sub

    ''
    '  DataItem class defines data items for technology package.
    '
    Public NotInheritable Class DataItem
        Private m_DataFormatId As String
        Private m_DataName As String
        Private m_Release As String
        Private m_DataId As String
        Private m_ProcessInstruction As String


        Public Property ProcessInstruction() As String
            Get
                ProcessInstruction = m_ProcessInstruction
            End Get

            Set(ByVal Value As String)
                m_ProcessInstruction = Value
            End Set

        End Property

        ''
        ' Gets and sets value for DataFormatId parameter. 
        ' DataFormatId defines name for data format.
        '
        ' @param Value Specifies value of DataFormatId parameter
        ' @return Value of DataFormatId parameter
        Public Property DataFormatId() As String
            Get
                DataFormatId = m_DataFormatId
            End Get

            Set(ByVal Value As String)
                m_DataFormatId = Value
            End Set

        End Property

        ''
        ' Gets and sets value for DataName parameter. 
        ' DataName defines name for data item.
        '
        ' @param Value Specifies value of DataName parameter
        ' @return Value of DataName parameter
        Public Property DataName() As String
            Get
                DataName = m_DataName
            End Get

            Set(ByVal Value As String)
                m_DataName = Value
            End Set

        End Property

        ' Gets and sets value for Release parameter. 
        ' Release defines release that introduced data item.
        '
        ' @param Value Specifies value of Release parameter
        ' @return Value of Release parameter
        Public Property Release() As String
            Get
                Release = m_Release
            End Get

            Set(ByVal Value As String)
                m_Release = Value
            End Set

        End Property

        ' Gets and sets value for DataId parameter. 
        ' DataId defines source identification for data item.
        '
        ' @param Value Specifies value of DataId parameter
        ' @return Value of DataId parameter
        Public Property DataId() As String
            Get
                DataId = m_DataId
            End Get

            Set(ByVal Value As String)
                m_DataId = Value
            End Set

        End Property

    End Class
End Class
