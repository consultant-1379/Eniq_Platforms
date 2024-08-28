''
' Gets a list of the universes in a domain on a BO server.
' "listUniverses" command.
Public Class GetDomainUnvListOp
    Inherits AbstractOperation

    Private DesignerApp As Designer.Application

    Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String)
        MyBase.New(operationName, bouser, bopass, borep, tpident, cmTechPack, baseident, outputFolder, eniqConn, boVersion, boAut, domain, universe)
    End Sub

    Public Overrides Function cleanup() As Boolean
        DesignerApp.Quit()
        DesignerApp = Nothing
    End Function

    Public Overrides Function doOperation() As Boolean
        Dim universe As Designer.StoredUniverse
        'Dim group As Designer.User  *** BOXI NOT SUPPORT THIS ***

        Try
            DesignerApp = logOnToDesignerApp()
            If m_domain <> "" Then
                'For Each universe In DesignerApp.UniverseDomains(domain).StoredUniverses *** FORMAT BO 6.5  ***
                For Each universe In DesignerApp.UniverseRootFolder.Folders(m_domain).StoredUniverses
                    Console.WriteLine(universe.Name)
                Next
            End If
            ' BOXI NOT SUPPORT GROUP STRUCTURE
            'Console.WriteLine("Groups:")
            'If domain <> "" Then
            'For Each group In DesignerApp.UniverseDomains(domain).Users


            'Console.WriteLine(group.Name)
            'Next
            'End If
        Catch ex As Exception
            Console.WriteLine("Error getting domain universe list: " & ex.ToString())
        End Try
        Return True
    End Function

    Protected Overrides Sub displayLogFileMessage()
        ' This command is used internally by IDE, so should not display a message box.
    End Sub

    Protected Overrides Sub setupTracing()
        ' This command is used internally by IDE, so should not create a log.
    End Sub
End Class
