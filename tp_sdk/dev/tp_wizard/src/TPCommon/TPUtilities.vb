Option Strict Off

''
' TPUtilities class is a collection of support functions for technology package creation.
'
Public NotInheritable Class TPUtilities

    Public Datatype As String
    Public Datasize As String
    Public Datascale As String

    ''
    ' Gets data type information for given data type definition.
    ' Sets values for Datatype, Datasize and Datascale.
    '
    ' @param Value Specified the data type definition
    Public Sub getDatatype(ByRef Value As String)

        Dim TempField3 As Integer
        Dim TempField2 As String
        Dim TempField1 As String
        Dim ReplaceArg As Double
        'Datatype
        If InStrRev(UCase(Value), "UNSIGNED INT") > 0 Then
            Datatype = "unsigned int"
        ElseIf InStrRev(UCase(Value), "SMALLINT") > 0 Then
            Datatype = "smallint"
        ElseIf InStrRev(UCase(Value), "TINYINT") > 0 Then
            Datatype = "tinyint"
        ElseIf InStrRev(UCase(Value), "VARCHAR2") > 0 Then
            Datatype = "varchar2"
        ElseIf InStrRev(UCase(Value), "DOUBLE") > 0 Then
            Datatype = "double"
        ElseIf InStrRev(UCase(Value), "INT") > 0 Then
            Datatype = "int"
        ElseIf InStrRev(UCase(Value), "DATETIME") > 0 Then
            Datatype = "datetime"
        ElseIf InStrRev(UCase(Value), "DATE") > 0 Then
            Datatype = "date"
        ElseIf InStrRev(UCase(Value), "VARCHAR") > 0 Then
            Datatype = "varchar"
        ElseIf InStrRev(UCase(Value), "CHAR") > 0 Then
            Datatype = "char"
        ElseIf InStrRev(UCase(Value), "FLOAT") > 0 Then
            Datatype = "float"
        ElseIf InStrRev(UCase(Value), "LONG") > 0 Then
            Datatype = "long"
        ElseIf InStrRev(UCase(Value), "NUMERIC") > 0 Then
            Datatype = "numeric"
        Else
            Datatype = "NOT FOUND"
        End If

        'Temp Fields
        TempField3 = InStrRev(Value, "(")
        If TempField3 <= 0 Then
            TempField3 = 9999999
            TempField2 = "0"
            TempField1 = "0"
        Else
            TempField2 = Value.Substring(TempField3)
            ReplaceArg = InStrRev(TempField2, ")")
            If ReplaceArg <= 0 Then
                ReplaceArg = 9999999
            End If
            TempField1 = Replace(TempField2, ")", "")
        End If

        'Datasize
        If InStrRev(Datatype, "int") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(Datatype, "date") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(Datatype, "double") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(UCase(Datatype), "NUMERIC") > 0 Then
            If InStrRev(TempField1, ",") > 0 Then
                Datasize = Left(TempField1, InStrRev(TempField1, ",") - 1)
            Else
                If TempField1 = "0" Then
                    Datasize = "Err"
                Else
                    Datasize = TempField1
                End If
            End If

        ElseIf InStrRev(UCase(Datatype), "CHAR") > 0 Then
            If TempField1 = "0" Then
                Datasize = "Err"
            Else
                Datasize = TempField1
            End If
        Else
            Datasize = "0"
        End If
        'Datascale
        If InStrRev(UCase(Datatype), "FLOAT") > 0 Then
            Datascale = "0"
        ElseIf InStrRev(UCase(Datatype), "DOUBLE") > 0 Then
            Datascale = "0"
        ElseIf InStrRev(UCase(Datatype), "NUMERIC") > 0 Then
            If InStrRev(TempField1, ",") > 0 Then
                Datascale = Right(TempField1, Len(TempField1) - InStrRev(TempField1, ","))
            Else
                Datascale = "0"
            End If
        Else
            Datascale = "0"
        End If

    End Sub

    ''
    ' Reads single value from TP definition.
    '
    ' @param testSheet Specified the value's location in definition
    ' @param conn Specifies reference to OLE DbConnection
    ' @param dbCommand Specifies reference to OLE DbCommand
    ' @param dbReader Specifies reference to OLE DbDataReader
    ' @return Value read from TP Definition
    Public Function readSingleValue(ByRef testSheet As String, ByRef conn As System.Data.OleDb.OleDbConnection, ByRef dbCommand As System.Data.OleDb.OleDbCommand, ByRef dbReader As System.Data.OleDb.OleDbDataReader) As String

        Dim Value As String
        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [" & testSheet & "]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            Value = Trim(dbReader.GetValue(0).ToString())
        End While
        dbReader.Close()
        dbCommand.Dispose()

        Return Value

    End Function

End Class
