Option Strict Off
Option Explicit On

Module frmMain

    ' the "official" entry point 
    Sub Main()
        ' call the Main with arguments
        Main(Environment.GetCommandLineArgs())
    End Sub

    ' the "real" Main procedure
    Private Sub Main(ByVal args() As String)
        ' suppose that we expect two arguments, but there is always an
        ' extra argument that contains the EXE filename
        If args Is Nothing OrElse args.Length = 1 Then
            Console.WriteLine("Ericsson Network IQ Tech Pack IDE - BO Interface, build " & GetEXEVersion())
            printHelp()
        Else
            Dim c As Integer
            Dim s As String
            Dim boUser As String
            Dim boPass As String
            Dim boRep As String
            Dim command As String
            Dim domain As String
            Dim group As String
            Dim universe As String
            Dim eniqConn As String
            Dim tpIdent As String
            Dim baseIdent As String
            Dim outputFolder As String
            Dim boVersion As String
            Dim boAut As String
            c = 0
            For Each s In args
                Select Case c ' Evaluate Number.
                    Case 0
                        'Application
                    Case 1
                        command = s
                    Case 2
                        boUser = s
                    Case 3
                        boPass = s
                    Case 4
                        boRep = s
                    Case 5
                        If command = "createUnv" OrElse command = "updateUnv" OrElse command = "updateEbsUnv" OrElse command = "createRep" OrElse command = "createDoc" Then
                            eniqConn = s
                        ElseIf command = "listUniverses" Then
                            domain = s
                        ElseIf command = "listDomain" Then
                            boVersion = s
                        Else
                            Console.WriteLine("Unknown attribute: " + s)
                            printHelp()
                        End If
                    Case 6
                        If command = "createUnv" OrElse command = "updateUnv" OrElse command = "updateEbsUnv" OrElse command = "createRep" OrElse command = "createDoc" Then
                            tpIdent = s
                        ElseIf command = "listDomain" Then
                            boAut = s
                        ElseIf command = "listUniverses" Then
                            boVersion = s
                        Else
                            Console.WriteLine("Unknown attribute: " + s)
                            printHelp()
                        End If
                    Case 7
                        If command = "createUnv" OrElse command = "updateUnv" OrElse command = "updateEbsUnv" OrElse command = "createRep" OrElse command = "createDoc" Then
                            baseIdent = s
                        ElseIf command = "listUniverses" Then
                            boAut = s
                        Else
                            Console.WriteLine("Unknown attribute: " + s)
                            printHelp()
                        End If
                    Case 8
                        If command = "createUnv" OrElse command = "updateUnv" OrElse command = "updateEbsUnv" OrElse command = "createRep" OrElse command = "createDoc" Then
                            outputFolder = s
                        Else
                            Console.WriteLine("Unknown attribute: " + s)
                            printHelp()
                        End If
                    Case 9
                        If command = "createUnv" OrElse command = "updateUnv" OrElse command = "updateEbsUnv" OrElse command = "createRep" OrElse command = "createDoc" Then
                            boVersion = s
                        Else
                            Console.WriteLine("Unknown attribute: " + s)
                            printHelp()
                        End If
                    Case 10
                        If command = "createUnv" OrElse command = "updateUnv" OrElse command = "updateEbsUnv" OrElse command = "createRep" OrElse command = "createDoc" Then
                            boAut = s
                        Else
                            Console.WriteLine("Unknown attribute: " + s)
                            printHelp()
                        End If
                    Case Else   ' Other values.
                        Console.WriteLine("Unknown attribute: " + s)
                        printHelp()
                End Select
                c += 1
            Next

            ' Do the operation:
            Try
                Dim operationFactory As New OperationFactory()
                Dim operation As AbstractOperation = operationFactory.createOperation(command, boUser, boPass, boRep, tpIdent, False, _
                                                                                      baseIdent, outputFolder, eniqConn, boVersion, boAut, domain, universe)
                operation.execute()
            Catch ex As Exception
                Console.WriteLine("Error executing command: " & ex.ToString())
                printHelp()
            End Try
        End If
    End Sub
    Private Sub printHelp()
        Console.WriteLine("Supported functionalities in TPIDE_BOIntf.exe are:")
        Console.WriteLine("")
        Console.WriteLine("Create tech pack universe. This is used to create a new tech pack data model (universe).")
        Console.WriteLine("There can be 0 to N universes in a tech pack.")
        Console.WriteLine("Syntax: TPIDE_BOIntf.exe createUnv {BO Username} {BO Password} {BO Repository} {ENIQ Repository ODBC Connection} {Tech Pack Identification} {Base Identification} {Output Folder} {BO Version} {Authentication}")
        Console.WriteLine("Attributes:")
        Console.WriteLine("createUnv - Command for creating tech pack universe(s)")
        Console.WriteLine("{BO Username} - Username to connect to BusinessObjects repository")
        Console.WriteLine("{BO Password} - Password to connect to BusinessObjects repository")
        Console.WriteLine("{BO Repository} - Connection information to BusinessObjects Repository, in format <server_name>:6400")
        Console.WriteLine("{ENIQ Repository ODBC Connection} - Connection information to ENIQ Repository, given as ODBC connection name")
        Console.WriteLine("{Tech Pack Identification} - Identification of the used tech pack")
        Console.WriteLine("{Base Identification} - Identification of the used base tech pack")
        Console.WriteLine("{Output Folder} - Directory where output is stored. Universe is saved under unv-directory under given directory")
        Console.WriteLine("{BO Version} - Used BusinessObjects version, use 6.5 or XI")
        Console.WriteLine("{Authentication} - Authentication method used to connect to BusinessObjects Repository, use ENTERPRISE")
        Console.WriteLine("")
        Console.WriteLine("")

        Console.WriteLine("Update tech pack universe. This is used to update existing tech pack data model (universe).")
        Console.WriteLine("There can be 0 to N universes in a tech pack.")
        Console.WriteLine("Syntax: TPIDE_BOIntf.exe updateUnv {BO Username} {BO Password} {BO Repository} {ENIQ Repository ODBC Connection} {Tech Pack Identification} {Base Identification} {Output Folder} {BO Version} {Authentication}")
        Console.WriteLine("Attributes:")
        Console.WriteLine("updateUnv - Command for updating tech pack universe(s)")
        Console.WriteLine("{BO Username} - Username to connect to BusinessObjects repository")
        Console.WriteLine("{BO Password} - Password to connect to BusinessObjects repository")
        Console.WriteLine("{BO Repository} - Connection information to BusinessObjects Repository, in format <server_name>:6400")
        Console.WriteLine("{ENIQ Repository ODBC Connection} - Connection information to ENIQ Repository, given as ODBC connection name")
        Console.WriteLine("{Tech Pack Identification} - Identification of the used tech pack")
        Console.WriteLine("{Base Identification} - Identification of the used base tech pack")
        Console.WriteLine("{Output Folder} - Directory where output is stored. Universe is saved under unv-directory under given directory")
        Console.WriteLine("{BO Version} - Used BusinessObjects version, use 6.5 or XI")
        Console.WriteLine("{Authentication} - Authentication method used to connect to BusinessObjects Repository, use ENTERPRISE")
        Console.WriteLine("")
        Console.WriteLine("")

        Console.WriteLine("Update EBS tech pack universe. This is used to update existing EBS tech pack data model (universe).")
        Console.WriteLine("EBS tech packs are specific tech packs, which universes are updated on run-time.")
        Console.WriteLine("Syntax: TPIDE_BOIntf.exe updateEbsUnv {BO Username} {BO Password} {BO Repository} {ENIQ Repository ODBC Connection} {Tech Pack Identification} {Base Identification} {Output Folder} {BO Version} {Authentication}")
        Console.WriteLine("Attributes:")
        Console.WriteLine("updateEbsUnv - Command for updating EBS tech pack universe")
        Console.WriteLine("{BO Username} - Username to connect to BusinessObjects repository")
        Console.WriteLine("{BO Password} - Password to connect to BusinessObjects repository")
        Console.WriteLine("{BO Repository} - Connection information to BusinessObjects Repository, in format <server_name>:6400")
        Console.WriteLine("{ENIQ Repository ODBC Connection} - Connection information to ENIQ Repository, given as ODBC connection name")
        Console.WriteLine("{Tech Pack Identification} - Identification of the used tech pack")
        Console.WriteLine("{Base Identification} - Identification of the used base tech pack")
        Console.WriteLine("{Output Folder} - Directory where output is stored. Universe is saved under unv-directory under given directory")
        Console.WriteLine("{BO Version} - Used BusinessObjects version, use 6.5 or XI")
        Console.WriteLine("{Authentication} - Authentication method used to connect to BusinessObjects Repository, use ENTERPRISE")
        Console.WriteLine("")
        Console.WriteLine("")

        Console.WriteLine("Create tech pack verification reports. This is used to create tech pack verification reports as BusinessIntelligence (.rep) reports.")
        Console.WriteLine("There can be 1 to N verification reports in a tech pack.")
        Console.WriteLine("Syntax: TPIDE_BOIntf.exe createRep {BO Username} {BO Password} {BO Repository} {ENIQ Repository ODBC Connection} {Tech Pack Identification} {Base Identification} {Output Folder} {BO Version} {Authentication}")
        Console.WriteLine("Attributes:")
        Console.WriteLine("createRep - Command for creating verification reports")
        Console.WriteLine("{BO Username} - Username to connect to BusinessObjects repository")
        Console.WriteLine("{BO Password} - Password to connect to BusinessObjects repository")
        Console.WriteLine("{BO Repository} - Connection information to BusinessObjects Repository, in format <server_name>:6400")
        Console.WriteLine("{ENIQ Repository ODBC Connection} - Connection information to ENIQ Repository, given as ODBC connection name")
        Console.WriteLine("{Tech Pack Identification} - Identification of the used tech pack")
        Console.WriteLine("{Base Identification} - Identification of the used base tech pack")
        Console.WriteLine("{Output Folder} - Directory where output is stored. Reports are saved under rep-directory under given directory")
        Console.WriteLine("{BO Version} - Used BusinessObjects version, use 6.5 or XI")
        Console.WriteLine("{Authentication} - Authentication method used to connect to BusinessObjects Repository, use ENTERPRISE")
        Console.WriteLine("")
        Console.WriteLine("")

        Console.WriteLine("Create tech pack universe reference document. This is used to create universe reference document as SDIF format for CPI.")
        Console.WriteLine("There is one reference document per universe.")
        Console.WriteLine("Syntax: TPIDE_BOIntf.exe createDoc {BO Username} {BO Password} {BO Repository} {ENIQ Repository ODBC Connection} {Tech Pack Identification} {Base Identification} {Output Folder} {BO Version} {Authentication}")
        Console.WriteLine("Attributes:")
        Console.WriteLine("createDoc - Command for creating universe reference")
        Console.WriteLine("{BO Username} - Username to connect to BusinessObjects repository")
        Console.WriteLine("{BO Password} - Password to connect to BusinessObjects repository")
        Console.WriteLine("{BO Repository} - Connection information to BusinessObjects Repository, in format <server_name>:6400")
        Console.WriteLine("{ENIQ Repository ODBC Connection} - Connection information to ENIQ Repository, given as ODBC connection name")
        Console.WriteLine("{Tech Pack Identification} - Identification of the used tech pack")
        Console.WriteLine("{Base Identification} - Identification of the used base tech pack")
        Console.WriteLine("{Output Folder} - Directory where output is stored. Documents are saved under doc-directory under given directory")
        Console.WriteLine("{BO Version} - Used BusinessObjects version, use 6.5 or XI")
        Console.WriteLine("{Authentication} - Authentication method used to connect to BusinessObjects Repository, use ENTERPRISE")
        Console.WriteLine("")
        Console.WriteLine("")

        Console.WriteLine("List universe folders in BusinessObjects Repository.")
        Console.WriteLine("Syntax: TPIDE_BOIntf.exe listDomain {BO Username} {BO Password} {BO Repository} {BO Version} {BO Authentication}")
        Console.WriteLine("Attributes:")
        Console.WriteLine("listDomain - Command for listing universe folders")
        Console.WriteLine("{BO Username} - Username to connect to BusinessObjects repository")
        Console.WriteLine("{BO Password} - Password to connect to BusinessObjects repository")
        Console.WriteLine("{BO Repository} - Connection information to BusinessObjects Repository, in format <server_name>:6400")
        Console.WriteLine("{BO Version} - Used BusinessObjects version, use 6.5 or XI")
        Console.WriteLine("{Authentication} - Authentication method used to connect to BusinessObjects Repository, use ENTERPRISE")
        Console.WriteLine("")
        Console.WriteLine("")

        Console.WriteLine("List universes in given universe folder folders in BusinessObjects Repository.")
        Console.WriteLine("Syntax: TPIDE_BOIntf.exe listUniverses {BO Username} {BO Password} {BO Repository} {Domain} {BO Version} {BO Authentication}")
        Console.WriteLine("Attributes:")
        Console.WriteLine("listUniverses - Command for listing universes")
        Console.WriteLine("{BO Username} - Username to connect to BusinessObjects repository")
        Console.WriteLine("{BO Password} - Password to connect to BusinessObjects repository")
        Console.WriteLine("{BO Repository} - Connection information to BusinessObjects Repository, in format <server_name>:6400")
        Console.WriteLine("{Domain} - Universe folder where to list universes")
        Console.WriteLine("{BO Version} - Used BusinessObjects version, use 6.5 or XI")
        Console.WriteLine("{Authentication} - Authentication method used to connect to BusinessObjects Repository, use ENTERPRISE")
        Console.WriteLine("")
        Console.WriteLine("")

    End Sub

    Private Function GetEXEVersion() As String
        With System.Diagnostics.FileVersionInfo.GetVersionInfo(System.Reflection.Assembly.GetExecutingAssembly.Location)
            Return .FileMajorPart & "." & .FileMinorPart & "." & .FileBuildPart & "." & .FilePrivatePart
        End With
    End Function

End Module
