''
' Creates a new linked universe.
Public Class LinkedUnvOp
    Inherits AbstractOperation

    Private TPInstallFunctionsClass As TPInstallFunctionsTPIDE = New TPInstallFunctionsTPIDE

    Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String)
        MyBase.New(operationName, bouser, bopass, borep, tpident, cmTechPack, baseident, outputFolder, eniqConn, boVersion, boAut, domain, universe)
        TPInstallFunctionsClass = New TPInstallFunctionsTPIDE()
    End Sub

    Public Overrides Function cleanup() As Boolean
        TPInstallFunctionsClass = Nothing
    End Function

    Public Overrides Function doOperation() As Boolean
        Dim UniverseFunctionsClass As UniverseFunctionsTPIde = New UniverseFunctionsTPIde
        Dim success As Boolean = False
        If m_domain <> "" AndAlso m_universe <> "" Then
            If (OperationName = "Create Linked Universe") Then
                success = TPInstallFunctionsClass.CreateLinkedUniverse(m_boUser, m_bopass, m_borep, m_universe, m_domain, True, m_boVersion, m_boAut)
            ElseIf (OperationName = "Update Linked Universe") Then
                success = TPInstallFunctionsClass.UpdateLinkedUniverse(m_boUser, m_bopass, m_borep, m_universe, m_domain, True, m_boVersion, m_boAut)
            End If
        End If
        Return success
    End Function
End Class
