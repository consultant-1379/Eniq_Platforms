'Public Class ReportProxy
'    Implements IReportProxy

'    ''
'    'Builds a table in a verification report.
'    '@param document
'    '@param boBlockStruc
'    '@param dataProvider
'    '@param tableName
'    '@param identifier
'    '@param dummyObjectName
'    'Public Sub buildRankBHTable(ByRef document As busobj.Document, ByRef boBlockStruc As busobj.BlockStructure, _
'    '                 ByRef dataProvider As busobj.DataProvider, ByVal tableName As String, ByVal identifier As String, _
'    '                 ByVal dummyObjectName As String) Implements IReportProxy.buildRankBHTable
'    '    Dim boPiv As busobj.Pivot
'    '    Dim pivotBodyItemIndex As Short

'    '    ''If boBlockStruc.Name = tableName Then
'    '    boPiv = boBlockStruc.Pivot
'    '    'Add day counters to table
'    '    For pivotBodyItemIndex = 1 To dataProvider.Columns.Count
'    '        Try
'    '            If InStrRev(dataProvider.Columns(pivotBodyItemIndex).Name, "(none)") = 0 Then
'    '                ' If "(none)" is not found:
'    '                boPiv.Body(pivotBodyItemIndex) = document.DocumentVariables(dataProvider.Columns(pivotBodyItemIndex).Name & identifier) ' e.g. "(DayBH)"
'    '            End If
'    '        Catch ex As Exception
'    '            Try
'    '                boPiv.Body(pivotBodyItemIndex) = document.DocumentVariables(dataProvider.Columns(pivotBodyItemIndex).Name)
'    '            Catch e As Exception
'    '                Trace.WriteLine("Error in '" & dataProvider.Columns(pivotBodyItemIndex).Name & "' at '" & dataProvider.Name & "'.")
'    '                Trace.WriteLine("Class Exception: " & e.ToString)
'    '            End Try
'    '        End Try
'    '    Next pivotBodyItemIndex
'    '    removeDummyObject(dummyObjectName, boPiv)
'    '    boPiv.Apply()
'    '    ''End If
'    'End Sub

'    ''
'    '@param name
'    '@param boPiv
'    Public Sub removeDummyObject(ByVal name As String, ByRef boPiv As busobj.Pivot) Implements IReportProxy.removeDummyObject

'        Dim pivotBodyItemIndex As Short
'        'remove "dummy" object from the table
'        For pivotBodyItemIndex = 1 To boPiv.BodyCount
'            If boPiv.Body(pivotBodyItemIndex).Name = name Then
'                boPiv.Body(pivotBodyItemIndex).Delete()
'                boPiv.Apply()
'                Exit For
'            End If
'        Next pivotBodyItemIndex
'    End Sub

'    ''
'    ''
'    '@param dataProvider
'    '@param boBlockStruc
'    '@param document
'    Public Sub addVariableToTable(ByVal dataProvider As busobj.DataProvider, ByVal boBlockStruc As busobj.BlockStructure, _
'                                  ByVal document As busobj.Document) Implements IReportProxy.addVariableToTable

'        Dim boPiv As busobj.Pivot = boBlockStruc.Pivot
'        Dim index As Integer = (dataProvider.Columns.Count + 1)
'        boPiv.Body(index) = document.DocumentVariables("BHCriteria")
'        boPiv.Apply()
'    End Sub
'End Class

