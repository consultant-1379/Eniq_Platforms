''
' Lists the contexts in a universe.
Public Class ListUniversesOp
    Inherits AbstractOperation

    Private UniverseFunctionsClass As UniverseFunctionsTPIde

    Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String)
        MyBase.New(operationName, bouser, bopass, borep, tpident, cmTechPack, baseident, outputFolder, eniqConn, boVersion, boAut, universe, domain)
        UniverseFunctionsClass = New UniverseFunctionsTPIde
    End Sub

    Public Overrides Function cleanup() As Boolean
        UniverseFunctionsClass.cleanup()
        UniverseFunctionsClass = Nothing
    End Function

    Public Overrides Function doOperation() As Boolean
        Dim success As Boolean = False
        success = UniverseFunctionsClass.Universe_ListContexts()
        Return success
    End Function

    Protected Overrides Sub displayLogFileMessage()
        ' This command is used internally by IDE, so should not display a message box.
    End Sub

    Protected Overrides Sub setupTracing()
        ' This command is used internally by IDE, so should not create a log.
    End Sub

End Class

