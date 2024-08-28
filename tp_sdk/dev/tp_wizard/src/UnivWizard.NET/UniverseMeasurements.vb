Option Strict Off

''
'  UnivClasses class is a collection of UnivClass classes
'
Public Class UniverseMeasurements
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of UnivClass classes in UnivClasses class
    '
    ' @param Index Specifies the index in the UnivClasses class
    ' @return Count of UnivClass classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets UnivClass class from UnivClasses class based on given index.
    '
    ' @param Index Specifies the index in the UnivClasses class
    ' @return Reference to UnivClass
    Public ReadOnly Property Item(ByVal Index As Integer) As UniverseMeasurement
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_items.Item(Index - 1), UniverseMeasurement)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds UnivClass class to UnivClasses class
    '
    ' @param ValueIn Specifies reference to UnivClass
    Public Sub AddItem(ByVal ValueIn As UniverseMeasurement)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    ''
    '  BOObject defines universe's classes.
    '
    Public Class UniverseMeasurement
        Private m_MeasurementTypes As MeasurementTypes
        Private m_UniverseExtension As String
        Private m_UniverseNameExtension As String
        Private m_UnivJoins As UnivJoins
        Private m_ReferenceTypes As ReferenceTypes
        Private m_ReferenceDatas As ReferenceDatas
        Private m_VectorReferenceTypes As ReferenceTypes
        Private m_VectorReferenceDatas As ReferenceDatas


        Public Property UniverseExtension()
            Get
                UniverseExtension = m_UniverseExtension
            End Get

            Set(ByVal Value)
                m_UniverseExtension = Value
            End Set

        End Property

        Public Property UniverseNameExtension()
            Get
                UniverseNameExtension = m_UniverseNameExtension
            End Get

            Set(ByVal Value)
                m_UniverseNameExtension = LCase(Value)
            End Set

        End Property

        Public Property MeasurementTypes()
            Get
                MeasurementTypes = m_MeasurementTypes
            End Get

            Set(ByVal Value)
                m_MeasurementTypes = Value
            End Set

        End Property

        Public Property UnivJoins()
            Get
                UnivJoins = m_UnivJoins
            End Get

            Set(ByVal Value)
                m_UnivJoins = Value
            End Set

        End Property

        Public Property ReferenceTypes()
            Get
                ReferenceTypes = m_ReferenceTypes
            End Get

            Set(ByVal Value)
                m_ReferenceTypes = Value
            End Set

        End Property

        Public Property ReferenceDatas()
            Get
                ReferenceDatas = m_ReferenceDatas
            End Get

            Set(ByVal Value)
                m_ReferenceDatas = Value
            End Set

        End Property

        Public Property VectorReferenceTypes()
            Get
                VectorReferenceTypes = m_VectorReferenceTypes
            End Get

            Set(ByVal Value)
                m_VectorReferenceTypes = Value
            End Set

        End Property

        Public Property VectorReferenceDatas()
            Get
                VectorReferenceDatas = m_VectorReferenceDatas
            End Get

            Set(ByVal Value)
                m_VectorReferenceDatas = Value
            End Set

        End Property

    End Class

End Class

