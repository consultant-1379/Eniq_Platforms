Option Strict Off

''
'  Transformations class is a collection of Transformation classes
'
Public NotInheritable Class InterfaceMeasurements
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As InterfaceMeasurement
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_items.Item(Index - 1), InterfaceMeasurement)
            End If
            Return Nothing
        End Get
    End Property

    Public Sub AddItem(ByVal ValueIn As InterfaceMeasurement)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    ''
    '  DataItem class defines data items for technology package.
    '
    Public NotInheritable Class InterfaceMeasurement
        Private m_TagId As String
        Private m_DataFormatId As String
        Private m_InterfaceName As String
        Private m_TransformerId As String
        Private m_Status As Integer
        Private m_Description As String
        Private m_Release As String
        Private m_Measurement As String
        Private m_DataFormatType As String

        Public Property TagId() As String
            Get
                TagId = m_TagId
            End Get

            Set(ByVal Value As String)
                m_TagId = Value
            End Set

        End Property

        Public Property DataFormatId() As String
            Get
                DataFormatId = m_DataFormatId
            End Get

            Set(ByVal Value As String)
                m_DataFormatId = Value
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

        Public Property TransformerId() As String
            Get
                TransformerId = m_TransformerId
            End Get

            Set(ByVal Value As String)
                m_TransformerId = Value
            End Set

        End Property

        Public Property Status() As Integer
            Get
                Status = m_Status
            End Get

            Set(ByVal Value As Integer)
                m_Status = Value
            End Set

        End Property

        Public Property Description() As String
            Get
                Description = m_Description
            End Get

            Set(ByVal Value As String)
                m_Description = Value
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

        Public Property Measurement() As String
            Get
                Measurement = m_Measurement
            End Get

            Set(ByVal Value As String)
                m_Measurement = Value
            End Set

        End Property

        Public Property DataFormatType() As String
            Get
                DataFormatType = m_DataFormatType
            End Get

            Set(ByVal Value As String)
                m_DataFormatType = Value
            End Set

        End Property


    End Class
End Class


