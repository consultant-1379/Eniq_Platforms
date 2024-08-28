Imports System.Collections

Public Interface DBProxy

    Function read() As Boolean

    Function getValue(ByVal ordinal As Integer) As Object

    Function getString(ByVal ordinal As Integer) As String

    Function isDBNull(ByVal ordinal As Integer) As Boolean

    Function readSingleColumnFromDB(ByVal sqlStatement As String, ByVal duplicatesOk As Boolean) As ArrayList

    Function getDbCommand(ByVal sqlCommand As String, ByVal conn As System.Data.Odbc.OdbcConnection) As System.Data.Odbc.OdbcCommand

    Sub setupDatabaseReader(ByVal sqlCommand As String, ByVal conn As System.Data.Odbc.OdbcConnection)

    Sub closeDatabase()

    Sub closeConnection(ByRef conn As System.Data.Odbc.OdbcConnection)

    Sub openConnection(ByRef conn As System.Data.Odbc.OdbcConnection)

End Interface
