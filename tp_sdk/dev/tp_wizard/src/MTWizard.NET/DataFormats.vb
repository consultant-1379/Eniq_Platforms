Option Strict Off

''
'  DataFormats class is a collection of DataFormat classes
'
Public NotInheritable Class DataFormats
    Private _dataformats As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    ' Gets count of DataFormat classes in DataFormats class
    '
    ' @param Index Specifies the index in the DataFormats class
    ' @return Count of DataFormat classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _dataformats Is Nothing) Then
                Return _dataformats.Count
            End If
            Return 0
        End Get
    End Property

    ''
    ' Gets DataFormat class from DataFormats class based on given index.
    '
    ' @param Index Specifies the index in the DataFormats class
    ' @return Reference to DataFormat
    Public ReadOnly Property Item(ByVal Index As Integer) As DataFormat
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_dataformats.Item(Index - 1), DataFormat)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds DataFormat class to DataFormats class
    '
    ' @param ValueIn Specifies reference to DataFormat
    Public Sub AddItem(ByVal ValueIn As DataFormat)

        If (Not _dataformats Is Nothing) Then
            _dataformats.Add(ValueIn)
        End If

    End Sub

    ''
    '  DataFormat class defines data formats for technology package.
    '
    Public NotInheritable Class DataFormat
        Private m_DataFormatId As String
        Private m_TypeId As String
        Private m_VersionId As String
        Private m_ObjectType As String
        Private m_FolderName As String
        Private m_DataFormatType As String

        Private m_TagId As String
        Private m_Measurement As String
        Private m_Release As String
        Private m_Description As String

        Private m_DataItems As DataItems

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
        ' Gets and sets value for TypeId parameter. 
        ' TypeId defines type for data format.
        '
        ' @param Value Specifies value of TypeId parameter
        ' @return Value of TypeId parameter
        Public Property TypeId() As String
            Get
                TypeId = m_TypeId
            End Get

            Set(ByVal Value As String)
                m_TypeId = Value
            End Set

        End Property

        ''
        ' Gets and sets value for VersionId parameter. 
        ' VersionId defines version for data format.
        '
        ' @param Value Specifies value of VersionId parameter
        ' @return Value of VersionId parameter
        Public Property VersionId() As String
            Get
                VersionId = m_VersionId
            End Get

            Set(ByVal Value As String)
                m_VersionId = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ObjectType parameter. 
        ' ObjectType defines object type for data format.
        '
        ' @param Value Specifies value of ObjectType parameter
        ' @return Value of ObjectType parameter
        Public Property ObjectType() As String
            Get
                ObjectType = m_ObjectType
            End Get

            Set(ByVal Value As String)
                m_ObjectType = Value
            End Set

        End Property

        ''
        ' Gets and sets value for FolderName parameter. 
        ' FolderName defines folder name for data format.
        '
        ' @param Value Specifies value of FolderName parameter
        ' @return Value of FolderName parameter
        Public Property FolderName() As String
            Get
                FolderName = m_FolderName
            End Get

            Set(ByVal Value As String)
                m_FolderName = Value
            End Set

        End Property

        ''
        ' Gets and sets value for DataFormatType parameter. 
        ' DataFormatType defines data format type for data format.
        '
        ' @param Value Specifies value of DataFormatType parameter
        ' @return Value of DataFormatType parameter
        Public Property DataFormatType() As String
            Get
                DataFormatType = m_DataFormatType
            End Get

            Set(ByVal Value As String)
                m_DataFormatType = Value
            End Set

        End Property

        ''
        ' Gets and sets value for DataItems parameter. 
        ' DataItems defines data items for data format.
        '
        ' @param Value Specifies value of DataItems parameter
        ' @return Value of DataItems parameter
        Public Property DataItems() As DataItems
            Get
                DataItems = m_DataItems
            End Get

            Set(ByVal Value As DataItems)
                m_DataItems = Value
            End Set

        End Property

        ''
        ' Gets and sets value for TagId parameter. 
        ' TagId defines tag identification for data format.
        '
        ' @param Value Specifies value of TagId parameter
        ' @return Value of TagId parameter
        Public Property TagId() As String
            Get
                TagId = m_TagId
            End Get

            Set(ByVal Value As String)
                m_TagId = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Measurement parameter. 
        ' Measurement defines measurement for data format.
        '
        ' @param Value Specifies value of Measurement parameter
        ' @return Value of Measurement parameter
        Public Property Measurement() As String
            Get
                Measurement = m_Measurement
            End Get

            Set(ByVal Value As String)
                m_Measurement = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Release parameter. 
        ' Release defines release that introduced data format.
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

        ''
        ' Gets and sets value for Description parameter. 
        ' Description defines description for data format.
        '
        ' @param Value Specifies value of Description parameter
        ' @return Value of Description parameter
        Public Property Description() As String
            Get
                Description = m_Description
            End Get

            Set(ByVal Value As String)
                m_Description = Value
            End Set

        End Property

    End Class
End Class
