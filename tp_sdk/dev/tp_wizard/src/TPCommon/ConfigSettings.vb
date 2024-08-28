Option Strict Off

''
'  ConfigSettings class is a class for reading and writing configuration.
'
Public NotInheritable Class ConfigSettings

    Public aConfig As Configuration.ConfigurationSettings
    Dim doc As Xml.XmlDataDocument
    Dim node As Xml.XmlNode
    Dim elem As Xml.XmlElement

    ''
    '  Reads setting defined by given parameter from configuration. 
    '
    ' @param key Specifies parameter
    ' @return Setting value from configuration
    Public Function ReadSetting(ByRef key As String) As String
        doc = loadConfigDocument()
        Return aConfig.AppSettings(key)
    End Function

    ''
    '  Writes setting defined by given parameter to configuration. 
    '
    ' @param key Specifies parameter
    ' @param value Specifies parameter value
    Public Sub WriteSetting(ByRef key As String, ByRef value As String)
        'load config document for current assembly

        doc = loadConfigDocument()

        ' retrieve appSettings node
        node = doc.SelectSingleNode("//appSettings")

        If node Is Nothing Then
            Throw New InvalidOperationException("appSettings section not found in config file.")
        End If
        Try
            ' select the 'add' element that contains the key
            elem = node.SelectSingleNode(String.Format("//add[@key='{0}']", key))

            If elem Is Nothing Then
                ' key was not found so create the 'add' element 
                ' and set it's key/value attributes 
                elem = doc.CreateElement("add")
                elem.SetAttribute("key", key)
                elem.SetAttribute("value", value)
                node.AppendChild(elem)
            Else
                ' add value for key
                elem.SetAttribute("value", value)
            End If
            doc.Save(getConfigFilePath())
        Catch
            Throw
        End Try

    End Sub

    ''
    '  Removes setting defined by given parameter from configuration. 
    '
    ' @param key Specifies parameter
    Public Sub RemoveSetting(ByRef key As String)
        ' load config document for current assembly
        doc = loadConfigDocument()

        ' retrieve appSettings node
        node = doc.SelectSingleNode("//appSettings")

        Try
            If node Is Nothing = True Then
                Throw New InvalidOperationException("appSettings section not found in config file.")
            Else
                ' remove 'add' element with coresponding key
                node.RemoveChild(node.SelectSingleNode(String.Format("//add[@key='{0}']", key)))
                doc.Save(getConfigFilePath())
            End If
        Catch e As NullReferenceException
            Throw New Exception(String.Format("The key {0} does not exist.", key), e)
        End Try
    End Sub
    ''
    '  Loads configuration document. 
    '
    ' @return Reference to configuration document
    Private Function loadConfigDocument() As Xml.XmlDataDocument

        Try
            doc = New Xml.XmlDataDocument
            doc.Load(getConfigFilePath())
            Return doc

        Catch e As System.IO.FileNotFoundException
            Throw New Exception("No configuration file found.", e)
        End Try
    End Function
    ''
    '  Gets configuration document file path. 
    '
    ' @return File path of configuration document
    Private Function getConfigFilePath() As String
        Return System.Reflection.Assembly.GetExecutingAssembly().Location + ".config"
    End Function
End Class
