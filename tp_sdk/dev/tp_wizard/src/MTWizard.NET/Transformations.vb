Option Strict Off

''
'  Transformations class is a collection of Transformation classes
'
Public NotInheritable Class Transformations
    Private _transformations As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    ' Gets count of Transformation classes in Transformations class
    '
    ' @param Index Specifies the index in the Transformations class
    ' @return Count of Transformation classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _transformations Is Nothing) Then
                Return _transformations.Count
            End If
            Return 0
        End Get
    End Property

    ''
    ' Gets Transformation class from Transformations class based on given index.
    '
    ' @param Index Specifies the index in the Transformations class
    ' @return Reference to Transformation
    Public ReadOnly Property Item(ByVal Index As Integer) As Transformation
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_transformations.Item(Index - 1), Transformation)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds Transformation class to Transformations class
    '
    ' @param ValueIn Specifies reference to Transformation
    Public Sub AddItem(ByVal ValueIn As Transformation)

        If (Not _transformations Is Nothing) Then
            _transformations.Add(ValueIn)
        End If

    End Sub

    ''
    '  DataItem class defines data items for technology package.
    '
    Public NotInheritable Class Transformation
        Private m_TransformationId As String
        Private m_InterfaceName As String
        Private m_MeasurementTypeID As String
        Private m_Type As String
        Private m_Source As String
        Private m_Target As String
        Private m_Config As String
        Private m_Release As String

        Public Property TransformationId() As String
            Get
                TransformationId = m_TransformationId
            End Get

            Set(ByVal Value As String)
                m_TransformationId = Value
            End Set

        End Property

        Public Property InterfaceName() As String
            Get
                InterfaceName = m_InterfaceName
            End Get

            Set(ByVal Value As String)
                m_InterfaceName = Value
            End Set

        End Property

        Public Property MeasurementTypeID() As String
            Get
                MeasurementTypeID = m_MeasurementTypeID
            End Get

            Set(ByVal Value As String)
                m_MeasurementTypeID = Value
            End Set

        End Property

        Public Property Type() As String
            Get
                Type = m_Type
            End Get

            Set(ByVal Value As String)
                m_Type = Value
            End Set

        End Property

        Public Property Source() As String
            Get
                Source = m_Source
            End Get

            Set(ByVal Value As String)
                m_Source = Value
            End Set

        End Property

        Public Property Target() As String
            Get
                Target = m_Target
            End Get

            Set(ByVal Value As String)
                m_Target = Value
            End Set

        End Property

        Public Property Config() As String
            Get
                Config = m_Config
            End Get

            Set(ByVal Value As String)
                m_Config = Value
            End Set

        End Property

        Public Property Release() As String
            Get
                Release = m_Release
            End Get

            Set(ByVal Value As String)
                m_Release = Value
            End Set

        End Property

    End Class
End Class

