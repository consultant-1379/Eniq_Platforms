Imports System.Collections


''
' DatabaseProxy: an implementation of the DBProxy interface.
Public Class DatabaseProxy
    Implements DBProxy

    Private dbReader As System.Data.Odbc.OdbcDataReader
    Private dbCommand As System.Data.Odbc.OdbcCommand
    Private tpConn As System.Data.Odbc.OdbcConnection

    Public Sub New()
        'constructor
    End Sub

    Public Function read() As Boolean Implements DBProxy.read
        Return dbReader.Read()
    End Function

    Public Function getValue(ByVal ordinal As Integer) As Object Implements DBProxy.getValue
        Return dbReader.GetValue(ordinal)
    End Function

    Public Function getString(ByVal ordinal As Integer) As String Implements DBProxy.getString
        Return dbReader.GetString(ordinal)
    End Function

    Public Function isDBNull(ByVal ordinal As Integer) As Boolean Implements DBProxy.isDBNull
        Return dbReader.IsDBNull(ordinal)
    End Function

    ''
    ' Sets up database reader with SQL statement
    ' 
    '@param sqlCommand SQL command to pass to the database
    '@param conn Connection to the database
    '@return OdbcDataReader The reader 
    Public Function getDbCommand(ByVal sqlCommand As String, ByVal conn As System.Data.Odbc.OdbcConnection) _
                                    As System.Data.Odbc.OdbcCommand Implements DBProxy.getDbCommand
        dbCommand = New System.Data.Odbc.OdbcCommand(sqlCommand, conn)
        Return dbCommand
    End Function

    ''
    ' Executes database reader
    Public Sub setupDatabaseReader(ByVal sqlCommand As String, _
                             ByVal conn As System.Data.Odbc.OdbcConnection) Implements DBProxy.setupDatabaseReader

        If Not (dbReader Is Nothing) Then
            If dbReader.IsClosed = False Then
                dbReader.Close()
            End If
        End If

        Try
            dbCommand = getDbCommand(sqlCommand, conn)
            dbReader = dbCommand.ExecuteReader()
        Catch ex As Exception
            Throw New Exception("Database Exception: " & ex.ToString)
        End Try
    End Sub

    ''
    'Read values from the database.
    '
    '@param sqlStatement
    '@param duplicatesOk
    '@param tpConn
    '@returns An ArrayList of all of the values read.
    Public Function readSingleColumnFromDB(ByVal sqlStatement As String, ByVal duplicatesOk As Boolean) As ArrayList Implements DBProxy.readSingleColumnFromDB

        Dim valueRead As String
        Dim valuesList As ArrayList
        valuesList = New ArrayList

        ' Set up database reader with SQL statement:
        'Try
        'getDbCommand(sqlStatement, tpConn)
        'Catch ex As Exception
        'Throw New Exception("Error setting up database reader: " & ex.ToString)
        'End Try

        While (read())
            If getValue(0).ToString() = "" Then ' just read first value for now
                Trace.WriteLine("Found no results")
                Exit While
            Else
                valueRead = Trim(getValue(0).ToString())
                If (valuesList.Contains(valueRead) = False) Then
                    valuesList.Add(valueRead)
                ElseIf (duplicatesOk = True) Then
                    ' If you want duplicates, add it to the list anyway, even if it's there already:
                    valuesList.Add(valueRead)
                End If
            End If
        End While
        Return valuesList
    End Function


    Public Sub closeDatabase() Implements DBProxy.closeDatabase
        If Not (dbReader Is Nothing) Then
            dbReader.Close()
        End If

        If Not (dbCommand Is Nothing) Then
            dbCommand.Dispose()
        End If
    End Sub

    Public Sub closeConnection(ByRef conn As System.Data.Odbc.OdbcConnection) Implements DBProxy.closeConnection
        conn.Close()
    End Sub

    Public Sub openConnection(ByRef conn As System.Data.Odbc.OdbcConnection) Implements DBProxy.openConnection
        conn.Open()
    End Sub
End Class
