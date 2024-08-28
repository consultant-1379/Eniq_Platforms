Option Strict Off

''
'  DataFormats class is a collection of DataFormat classes
'
Public NotInheritable Class DataInterfaces
    Private _datainterfaces As System.Collections.ArrayList = New System.Collections.ArrayList

    Public ReadOnly Property Count() As Integer
        Get
            If (Not _datainterfaces Is Nothing) Then
                Return _datainterfaces.Count
            End If
            Return 0
        End Get
    End Property

    Public ReadOnly Property Item(ByVal Index As Integer) As DataInterface
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return DirectCast(_datainterfaces.Item(Index - 1), DataInterface)
            End If
            Return Nothing
        End Get
    End Property

    Public Sub AddItem(ByVal ValueIn As DataInterface)

        If (Not _datainterfaces Is Nothing) Then
            _datainterfaces.Add(ValueIn)
        End If

    End Sub

    ''
    '  DataFormat class defines data formats for technology package.
    '
    Public NotInheritable Class DataInterface
        Private m_InterfaceName As String
        Private m_Status As Integer
        Private m_InterfaceType As String
        Private m_Description As String
        Private m_Release As String

        Public Property InterfaceName() As String
            Get
                InterfaceName = m_InterfaceName
            End Get

            Set(ByVal Value As String)
                m_InterfaceName = Value
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

        Public Property InterfaceType() As String
            Get
                InterfaceType = m_InterfaceType
            End Get

            Set(ByVal Value As String)
                m_InterfaceType = Value
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

    End Class
End Class
