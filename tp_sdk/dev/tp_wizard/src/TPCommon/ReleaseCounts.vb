Option Strict Off

''
'  ReleaseCounts class is a collection of ReleaseCount classes
'
Public NotInheritable Class ReleaseCounts
    Private _counts As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of ReleaseCount classes in ReleaseCounts class
    '
    ' @param Index Specifies the index in the ReleaseCounts class
    ' @return Count of ReleaseCount classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _counts Is Nothing) Then
                Return _counts.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets ReleaseCount class from ReleaseCounts class based on given index.
    '
    ' @param Index Specifies the index in the ReleaseCounts class
    ' @return Reference to ReleaseCount
    Public ReadOnly Property Item(ByVal Index As Integer) As ReleaseCount
        Get
            If (Index > 0) AndAlso (Index <= Me.Count) Then
                Return CType(_counts.Item(Index - 1), ReleaseCount)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds ReleaseCount class to ReleaseCounts class
    '
    ' @param ValueIn Specifies reference to ReleaseCount
    Public Sub AddItem(ByVal ValueIn As ReleaseCount)

        If (Not _counts Is Nothing) Then
            _counts.Add(ValueIn)
        End If

    End Sub

    ''
    '  ReleaseCount class is used for counting column order numbers in measurement types
    '
    Public NotInheritable Class ReleaseCount
        Private m_Release As String
        Private m_ColCount As Integer

        ''
        ' Gets value for Release parameter.
        '
        ' @return Value of Release parameter
        Public ReadOnly Property Release() As String
            Get
                Release = m_Release
            End Get

        End Property

        ''
        ' Gets value for ColCount parameter. ColCount defines the used column order number.
        '
        ' @return Value of ColCount parameter
        Public ReadOnly Property ColCount() As Integer
            Get
                ColCount = m_ColCount
                m_ColCount += 1
            End Get

        End Property

        ''
        ' Sets seed value for column order numbers in specified release
        '
        ' @param Release Specifies DC5000 release
        ' @param SeedNum Specifies seed value
        ' @param Zeros Specifies number of extra zeros
        Public Sub SetSeed(ByVal Release As String, ByVal SeedNum As Integer, ByVal Zeros As String)
            Dim RelInfo() As String
            Dim SeedValue As Integer

            RelInfo = Split(Release, ".")
            m_Release = Release
            SeedValue = Int(CStr(10 * Int(RelInfo(2))) & CStr(100 * Int(RelInfo(3))) & Zeros & CStr(SeedNum))
            m_ColCount = SeedValue
        End Sub
    End Class
End Class
