Option Strict Off

''
'  ReportConditions class is a collection of ReportCondition classes
'
Public Class ReportConditions
    Private _reportconditions As System.Collections.ArrayList = New System.Collections.ArrayList

    ''
    '  Gets count of ReportCondition classes in ReportConditions class
    '
    ' @param Index Specifies the index in the ReportConditions class
    ' @return Count of ReportCondition classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _reportconditions Is Nothing) Then
                Return _reportconditions.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets ReportCondition class from ReportConditions class based on given index.
    '
    ' @param Index Specifies the index in the ReportConditions class
    ' @return Reference to ReportCondition
    Public ReadOnly Property Item(ByVal Index As Integer) As ReportCondition
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_reportconditions.Item(Index - 1), ReportCondition)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds ReportCondition class to ReportConditions class
    '
    ' @param ValueIn Specifies reference to ReportCondition
    Public Sub AddItem(ByVal ValueIn As ReportCondition)

        If (Not _reportconditions Is Nothing) Then
            _reportconditions.Add(ValueIn)
        End If

    End Sub

    ''
    '  ReportCondition defines reports's conditions.
    '
    Public Class ReportCondition
        Private m_MeasurementTypeID As String
        Private m_Level As String
        Private m_CondClass As String
        Private m_Name As String
        Private m_Prompt1 As String
        Private m_Value1 As String
        Private m_Prompt2 As String
        Private m_Value2 As String
        Private m_Prompt3 As String
        Private m_Value3 As String
        Private m_ObjectCondition As String

        ''
        ' Gets and sets value for MeasurementTypeID parameter. 
        ' MeasurementTypeID defines measurement type.
        '
        ' @param Value Specifies value of MeasurementTypeID parameter
        ' @return Value of MeasurementTypeID parameter
        Public Property MeasurementTypeID()
            Get
                MeasurementTypeID = m_MeasurementTypeID
            End Get

            Set(ByVal Value)
                m_MeasurementTypeID = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Level parameter. 
        ' Level defines measurement level.
        '
        ' @param Value Specifies value of Level parameter
        ' @return Value of Level parameter
        Public Property Level()
            Get
                Level = m_Level
            End Get

            Set(ByVal Value)
                m_Level = Value
            End Set

        End Property

        ''
        ' Gets and sets value for CondClass parameter. 
        ' CondClass defines condition's class level.
        '
        ' @param Value Specifies value of CondClass parameter
        ' @return Value of CondClass parameter
        Public Property CondClass()
            Get
                CondClass = m_CondClass
            End Get

            Set(ByVal Value)
                m_CondClass = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Name parameter. 
        ' Name defines condition's name.
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
        ' Gets and sets value for Prompt1 parameter. 
        ' Prompt1 defines condition's first prompt text.
        '
        ' @param Value Specifies value of Prompt1 parameter
        ' @return Value of Prompt1 parameter
        Public Property Prompt1()
            Get
                Prompt1 = m_Prompt1
            End Get

            Set(ByVal Value)
                m_Prompt1 = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Value1 parameter. 
        ' Value1 defines condition's first prompt value.
        '
        ' @param Value Specifies value of Value1 parameter
        ' @return Value of Value1 parameter
        Public Property Value1()
            Get
                Value1 = m_Value1
            End Get

            Set(ByVal Value)
                m_Value1 = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Prompt2 parameter. 
        ' Prompt2 defines condition's second prompt text.
        '
        ' @param Value Specifies value of Prompt2 parameter
        ' @return Value of Prompt2 parameter
        Public Property Prompt2()
            Get
                Prompt2 = m_Prompt2
            End Get

            Set(ByVal Value)
                m_Prompt2 = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Value2 parameter. 
        ' Value2 defines condition's second prompt value.
        '
        ' @param Value Specifies value of Value2 parameter
        ' @return Value of Value2 parameter
        Public Property Value2()
            Get
                Value2 = m_Value2
            End Get

            Set(ByVal Value)
                m_Value2 = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Prompt3 parameter. 
        ' Prompt3 defines condition's third prompt text.
        '
        ' @param Value Specifies value of Prompt3 parameter
        ' @return Value of Prompt3 parameter
        Public Property Prompt3()
            Get
                Prompt3 = m_Prompt3
            End Get

            Set(ByVal Value)
                m_Prompt3 = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Value3 parameter. 
        ' Value3 defines condition's third prompt value.
        '
        ' @param Value Specifies value of Value3 parameter
        ' @return Value of Value3 parameter
        Public Property Value3()
            Get
                Value3 = m_Value3
            End Get

            Set(ByVal Value)
                m_Value3 = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ObjectCondition parameter. 
        ' ObjectCondition defines whether conditions is actually object.
        '
        ' @param Value Specifies value of ObjectCondition parameter
        ' @return Value of ObjectCondition parameter
        Public Property ObjectCondition()
            Get
                ObjectCondition = m_ObjectCondition
            End Get

            Set(ByVal Value)
                m_ObjectCondition = LCase(Value)
            End Set

        End Property

    End Class

    Public Sub getReportConditions(ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader, ByRef logs As logMessages)

        Dim indexes() As String
        Dim count As Integer
        Dim repcond As ReportConditions.ReportCondition
        Dim Row As Integer

        Row = 1

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Report conditions$]", conn)
        Try
            dbReader = dbCommand.ExecuteReader()
        Catch ex As Exception
            logs.AddLogText("'Report conditions' sheet was not found in TP definition. Verification reports will not be generated correctly.")
            Exit Sub
        End Try
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Row += 1
                repcond = New ReportConditions.ReportCondition
                repcond.MeasurementTypeID = dbReader.GetValue(0).ToString()
                repcond.Level = dbReader.GetValue(1).ToString()
                repcond.CondClass = dbReader.GetValue(2).ToString()
                repcond.Name = dbReader.GetValue(3).ToString()
                repcond.Prompt1 = dbReader.GetValue(4).ToString()
                repcond.Value1 = dbReader.GetValue(5).ToString()
                repcond.Prompt2 = dbReader.GetValue(6).ToString()
                repcond.Value2 = dbReader.GetValue(7).ToString()
                repcond.ObjectCondition = dbReader.GetValue(8).ToString()
                repcond.Prompt3 = dbReader.GetValue(9).ToString()
                repcond.Value3 = dbReader.GetValue(10).ToString()
                AddItem(repcond)
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

    End Sub
End Class

