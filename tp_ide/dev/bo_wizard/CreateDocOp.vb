''
'Creates universe reference document for a tech pack.
Public Class CreateDocOp
    Inherits AbstractOperation

    Private docFunctions As DocumentFunctionsTPIde

    Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String)
        MyBase.New(operationName, bouser, bopass, borep, tpident, cmTechPack, baseident, outputFolder, eniqConn, boVersion, boAut, domain, universe)
        docFunctions = New DocumentFunctionsTPIde(New TPUtilitiesTPIde(), New UniverseDocumentWriter(tpident))
    End Sub

    Public Overrides Function cleanup() As Boolean
        docFunctions = Nothing
    End Function

    Public Overrides Function doOperation() As Boolean
        Dim success As Boolean = False
        success = docFunctions.GenerateTechPackProductReference(m_tpident, m_outputFolder, m_cmTechPack, m_boUser, m_bopass, m_borep, m_boVersion, m_boAut)
        Return success
    End Function
End Class
