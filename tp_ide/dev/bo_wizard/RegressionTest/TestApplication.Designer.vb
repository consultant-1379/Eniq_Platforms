<Global.Microsoft.VisualBasic.CompilerServices.DesignerGenerated()> _
Partial Class TestApplication
    Inherits System.Windows.Forms.Form

    'Form overrides dispose to clean up the component list.
    <System.Diagnostics.DebuggerNonUserCode()> _
    Protected Overrides Sub Dispose(ByVal disposing As Boolean)
        Try
            If disposing AndAlso components IsNot Nothing Then
                components.Dispose()
            End If
        Finally
            MyBase.Dispose(disposing)
        End Try
    End Sub

    'Required by the Windows Form Designer
    Private components As System.ComponentModel.IContainer

    'NOTE: The following procedure is required by the Windows Form Designer
    'It can be modified using the Windows Form Designer.  
    'Do not modify it using the code editor.
    <System.Diagnostics.DebuggerStepThrough()> _
    Private Sub InitializeComponent()
        Dim resources As System.ComponentModel.ComponentResourceManager = New System.ComponentModel.ComponentResourceManager(GetType(TestApplication))
        Me.DWHREPConCombo = New System.Windows.Forms.ComboBox
        Me.Label1 = New System.Windows.Forms.Label
        Me.boServerComboBox = New System.Windows.Forms.ComboBox
        Me.Label2 = New System.Windows.Forms.Label
        Me.CheckedListBox1 = New System.Windows.Forms.CheckedListBox
        Me.Label4 = New System.Windows.Forms.Label
        Me.CreateCheckBox = New System.Windows.Forms.CheckBox
        Me.UpdateCheckBox = New System.Windows.Forms.CheckBox
        Me.ReportsCheckBox = New System.Windows.Forms.CheckBox
        Me.ReferenceCheckBox = New System.Windows.Forms.CheckBox
        Me.CreateLinkedCheckBox = New System.Windows.Forms.CheckBox
        Me.UpdateLinkedCheckBox = New System.Windows.Forms.CheckBox
        Me.Button1 = New System.Windows.Forms.Button
        Me.Select_All = New System.Windows.Forms.Button
        Me.Unselect_All = New System.Windows.Forms.Button
        Me.Button2 = New System.Windows.Forms.Button
        Me.usernameTextBox = New System.Windows.Forms.TextBox
        Me.Label5 = New System.Windows.Forms.Label
        Me.passwordTextBox = New System.Windows.Forms.TextBox
        Me.Label6 = New System.Windows.Forms.Label
        Me.Label7 = New System.Windows.Forms.Label
        Me.Label8 = New System.Windows.Forms.Label
        Me.Label9 = New System.Windows.Forms.Label
        Me.outputDirTextBox = New System.Windows.Forms.TextBox
        Me.Label10 = New System.Windows.Forms.Label
        Me.boVersionComboBox = New System.Windows.Forms.ComboBox
        Me.baseTechPackCombo = New System.Windows.Forms.ComboBox
        Me.FolderBrowserDialog1 = New System.Windows.Forms.FolderBrowserDialog
        Me.folderBrowserButton = New System.Windows.Forms.Button
        Me.SuspendLayout()
        '
        'DWHREPConCombo
        '
        Me.DWHREPConCombo.FormattingEnabled = True
        Me.DWHREPConCombo.Location = New System.Drawing.Point(181, 18)
        Me.DWHREPConCombo.MaxDropDownItems = 90
        Me.DWHREPConCombo.Name = "DWHREPConCombo"
        Me.DWHREPConCombo.Size = New System.Drawing.Size(291, 21)
        Me.DWHREPConCombo.Sorted = True
        Me.DWHREPConCombo.TabIndex = 1
        '
        'Label1
        '
        Me.Label1.AutoSize = True
        Me.Label1.Location = New System.Drawing.Point(75, 21)
        Me.Label1.Name = "Label1"
        Me.Label1.Size = New System.Drawing.Size(98, 13)
        Me.Label1.TabIndex = 2
        Me.Label1.Text = "dwhrep connection"
        '
        'boServerComboBox
        '
        Me.boServerComboBox.FormattingEnabled = True
        Me.boServerComboBox.Location = New System.Drawing.Point(181, 56)
        Me.boServerComboBox.Name = "boServerComboBox"
        Me.boServerComboBox.Size = New System.Drawing.Size(291, 21)
        Me.boServerComboBox.TabIndex = 2
        '
        'Label2
        '
        Me.Label2.AutoSize = True
        Me.Label2.Location = New System.Drawing.Point(118, 59)
        Me.Label2.Name = "Label2"
        Me.Label2.Size = New System.Drawing.Size(56, 13)
        Me.Label2.TabIndex = 4
        Me.Label2.Text = "BO Server"
        '
        'CheckedListBox1
        '
        Me.CheckedListBox1.FormattingEnabled = True
        Me.CheckedListBox1.Location = New System.Drawing.Point(187, 289)
        Me.CheckedListBox1.Name = "CheckedListBox1"
        Me.CheckedListBox1.Size = New System.Drawing.Size(285, 244)
        Me.CheckedListBox1.TabIndex = 9
        '
        'Label4
        '
        Me.Label4.AutoSize = True
        Me.Label4.Location = New System.Drawing.Point(63, 289)
        Me.Label4.Name = "Label4"
        Me.Label4.Size = New System.Drawing.Size(96, 13)
        Me.Label4.TabIndex = 7
        Me.Label4.Text = "Tech packs to test"
        '
        'CreateCheckBox
        '
        Me.CreateCheckBox.AutoSize = True
        Me.CreateCheckBox.Location = New System.Drawing.Point(187, 557)
        Me.CreateCheckBox.Name = "CreateCheckBox"
        Me.CreateCheckBox.Size = New System.Drawing.Size(102, 17)
        Me.CreateCheckBox.TabIndex = 13
        Me.CreateCheckBox.Text = "Create Universe"
        Me.CreateCheckBox.UseVisualStyleBackColor = True
        '
        'UpdateCheckBox
        '
        Me.UpdateCheckBox.AutoSize = True
        Me.UpdateCheckBox.Location = New System.Drawing.Point(188, 594)
        Me.UpdateCheckBox.Name = "UpdateCheckBox"
        Me.UpdateCheckBox.Size = New System.Drawing.Size(106, 17)
        Me.UpdateCheckBox.TabIndex = 14
        Me.UpdateCheckBox.Text = "Update Universe"
        Me.UpdateCheckBox.UseVisualStyleBackColor = True
        '
        'ReportsCheckBox
        '
        Me.ReportsCheckBox.AutoSize = True
        Me.ReportsCheckBox.Location = New System.Drawing.Point(188, 631)
        Me.ReportsCheckBox.Name = "ReportsCheckBox"
        Me.ReportsCheckBox.Size = New System.Drawing.Size(152, 17)
        Me.ReportsCheckBox.TabIndex = 15
        Me.ReportsCheckBox.Text = "Create Verification Reports"
        Me.ReportsCheckBox.UseVisualStyleBackColor = True
        '
        'ReferenceCheckBox
        '
        Me.ReferenceCheckBox.AutoSize = True
        Me.ReferenceCheckBox.Enabled = False
        Me.ReferenceCheckBox.Location = New System.Drawing.Point(348, 557)
        Me.ReferenceCheckBox.Name = "ReferenceCheckBox"
        Me.ReferenceCheckBox.Size = New System.Drawing.Size(155, 17)
        Me.ReferenceCheckBox.TabIndex = 16
        Me.ReferenceCheckBox.Text = "Create Universe Reference"
        Me.ReferenceCheckBox.UseVisualStyleBackColor = True
        '
        'CreateLinkedCheckBox
        '
        Me.CreateLinkedCheckBox.AutoSize = True
        Me.CreateLinkedCheckBox.Enabled = False
        Me.CreateLinkedCheckBox.Location = New System.Drawing.Point(348, 594)
        Me.CreateLinkedCheckBox.Name = "CreateLinkedCheckBox"
        Me.CreateLinkedCheckBox.Size = New System.Drawing.Size(137, 17)
        Me.CreateLinkedCheckBox.TabIndex = 17
        Me.CreateLinkedCheckBox.Text = "Create Linked Universe"
        Me.CreateLinkedCheckBox.UseVisualStyleBackColor = True
        '
        'UpdateLinkedCheckBox
        '
        Me.UpdateLinkedCheckBox.AutoSize = True
        Me.UpdateLinkedCheckBox.Enabled = False
        Me.UpdateLinkedCheckBox.Location = New System.Drawing.Point(348, 631)
        Me.UpdateLinkedCheckBox.Name = "UpdateLinkedCheckBox"
        Me.UpdateLinkedCheckBox.Size = New System.Drawing.Size(141, 17)
        Me.UpdateLinkedCheckBox.TabIndex = 18
        Me.UpdateLinkedCheckBox.Text = "Update Linked Universe"
        Me.UpdateLinkedCheckBox.UseVisualStyleBackColor = True
        '
        'Button1
        '
        Me.Button1.Font = New System.Drawing.Font("Microsoft Sans Serif", 8.25!, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, CType(0, Byte))
        Me.Button1.Location = New System.Drawing.Point(497, 289)
        Me.Button1.Name = "Button1"
        Me.Button1.Size = New System.Drawing.Size(110, 27)
        Me.Button1.TabIndex = 10
        Me.Button1.Text = "Get Tech Packs"
        Me.Button1.UseVisualStyleBackColor = True
        '
        'Select_All
        '
        Me.Select_All.Location = New System.Drawing.Point(497, 333)
        Me.Select_All.Name = "Select_All"
        Me.Select_All.Size = New System.Drawing.Size(98, 31)
        Me.Select_All.TabIndex = 11
        Me.Select_All.Text = "Select All"
        Me.Select_All.UseVisualStyleBackColor = True
        '
        'Unselect_All
        '
        Me.Unselect_All.Location = New System.Drawing.Point(497, 382)
        Me.Unselect_All.Name = "Unselect_All"
        Me.Unselect_All.Size = New System.Drawing.Size(95, 28)
        Me.Unselect_All.TabIndex = 12
        Me.Unselect_All.Text = "Unselect All"
        Me.Unselect_All.UseVisualStyleBackColor = True
        '
        'Button2
        '
        Me.Button2.Location = New System.Drawing.Point(567, 611)
        Me.Button2.Name = "Button2"
        Me.Button2.Size = New System.Drawing.Size(157, 36)
        Me.Button2.TabIndex = 17
        Me.Button2.Text = "Start"
        Me.Button2.UseVisualStyleBackColor = True
        '
        'usernameTextBox
        '
        Me.usernameTextBox.Location = New System.Drawing.Point(180, 92)
        Me.usernameTextBox.Name = "usernameTextBox"
        Me.usernameTextBox.Size = New System.Drawing.Size(292, 20)
        Me.usernameTextBox.TabIndex = 3
        '
        'Label5
        '
        Me.Label5.AutoSize = True
        Me.Label5.Location = New System.Drawing.Point(119, 92)
        Me.Label5.Name = "Label5"
        Me.Label5.Size = New System.Drawing.Size(55, 13)
        Me.Label5.TabIndex = 20
        Me.Label5.Text = "Username"
        '
        'passwordTextBox
        '
        Me.passwordTextBox.Location = New System.Drawing.Point(180, 125)
        Me.passwordTextBox.Name = "passwordTextBox"
        Me.passwordTextBox.Size = New System.Drawing.Size(292, 20)
        Me.passwordTextBox.TabIndex = 4
        '
        'Label6
        '
        Me.Label6.AutoSize = True
        Me.Label6.Location = New System.Drawing.Point(121, 125)
        Me.Label6.Name = "Label6"
        Me.Label6.Size = New System.Drawing.Size(53, 13)
        Me.Label6.TabIndex = 23
        Me.Label6.Text = "Password"
        '
        'Label7
        '
        Me.Label7.AutoSize = True
        Me.Label7.Location = New System.Drawing.Point(133, 167)
        Me.Label7.Name = "Label7"
        Me.Label7.Size = New System.Drawing.Size(0, 13)
        Me.Label7.TabIndex = 24
        '
        'Label8
        '
        Me.Label8.AutoSize = True
        Me.Label8.Location = New System.Drawing.Point(86, 163)
        Me.Label8.Name = "Label8"
        Me.Label8.Size = New System.Drawing.Size(87, 13)
        Me.Label8.TabIndex = 25
        Me.Label8.Text = "Base Tech Pack"
        '
        'Label9
        '
        Me.Label9.AutoSize = True
        Me.Label9.Location = New System.Drawing.Point(90, 198)
        Me.Label9.Name = "Label9"
        Me.Label9.Size = New System.Drawing.Size(84, 13)
        Me.Label9.TabIndex = 27
        Me.Label9.Text = "Output Directory"
        '
        'outputDirTextBox
        '
        Me.outputDirTextBox.Location = New System.Drawing.Point(180, 194)
        Me.outputDirTextBox.Name = "outputDirTextBox"
        Me.outputDirTextBox.Size = New System.Drawing.Size(292, 20)
        Me.outputDirTextBox.TabIndex = 6
        '
        'Label10
        '
        Me.Label10.AutoSize = True
        Me.Label10.Location = New System.Drawing.Point(113, 227)
        Me.Label10.Name = "Label10"
        Me.Label10.Size = New System.Drawing.Size(60, 13)
        Me.Label10.TabIndex = 30
        Me.Label10.Text = "BO Version"
        '
        'boVersionComboBox
        '
        Me.boVersionComboBox.FormattingEnabled = True
        Me.boVersionComboBox.Items.AddRange(New Object() {"XI", "6.5"})
        Me.boVersionComboBox.Location = New System.Drawing.Point(181, 222)
        Me.boVersionComboBox.Name = "boVersionComboBox"
        Me.boVersionComboBox.Size = New System.Drawing.Size(290, 21)
        Me.boVersionComboBox.TabIndex = 8
        '
        'baseTechPackCombo
        '
        Me.baseTechPackCombo.FormattingEnabled = True
        Me.baseTechPackCombo.Location = New System.Drawing.Point(181, 160)
        Me.baseTechPackCombo.Name = "baseTechPackCombo"
        Me.baseTechPackCombo.Size = New System.Drawing.Size(289, 21)
        Me.baseTechPackCombo.TabIndex = 5
        '
        'folderBrowserButton
        '
        Me.folderBrowserButton.Location = New System.Drawing.Point(478, 194)
        Me.folderBrowserButton.Name = "folderBrowserButton"
        Me.folderBrowserButton.Size = New System.Drawing.Size(46, 23)
        Me.folderBrowserButton.TabIndex = 7
        Me.folderBrowserButton.Text = "..."
        Me.folderBrowserButton.UseVisualStyleBackColor = True
        '
        'TestApplication
        '
        Me.AutoScaleDimensions = New System.Drawing.SizeF(6.0!, 13.0!)
        Me.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font
        Me.ClientSize = New System.Drawing.Size(743, 691)
        Me.Controls.Add(Me.folderBrowserButton)
        Me.Controls.Add(Me.baseTechPackCombo)
        Me.Controls.Add(Me.boVersionComboBox)
        Me.Controls.Add(Me.Label10)
        Me.Controls.Add(Me.outputDirTextBox)
        Me.Controls.Add(Me.Label9)
        Me.Controls.Add(Me.Label8)
        Me.Controls.Add(Me.Label7)
        Me.Controls.Add(Me.Label6)
        Me.Controls.Add(Me.passwordTextBox)
        Me.Controls.Add(Me.Label5)
        Me.Controls.Add(Me.usernameTextBox)
        Me.Controls.Add(Me.Button2)
        Me.Controls.Add(Me.Unselect_All)
        Me.Controls.Add(Me.Select_All)
        Me.Controls.Add(Me.Button1)
        Me.Controls.Add(Me.UpdateLinkedCheckBox)
        Me.Controls.Add(Me.CreateLinkedCheckBox)
        Me.Controls.Add(Me.ReferenceCheckBox)
        Me.Controls.Add(Me.ReportsCheckBox)
        Me.Controls.Add(Me.UpdateCheckBox)
        Me.Controls.Add(Me.CreateCheckBox)
        Me.Controls.Add(Me.Label4)
        Me.Controls.Add(Me.CheckedListBox1)
        Me.Controls.Add(Me.Label2)
        Me.Controls.Add(Me.boServerComboBox)
        Me.Controls.Add(Me.Label1)
        Me.Controls.Add(Me.DWHREPConCombo)
        Me.Icon = CType(resources.GetObject("$this.Icon"), System.Drawing.Icon)
        Me.Name = "TestApplication"
        Me.Text = "Universe Regression test"
        Me.ResumeLayout(False)
        Me.PerformLayout()

    End Sub
    Friend WithEvents Label1 As System.Windows.Forms.Label
    Friend WithEvents boServerComboBox As System.Windows.Forms.ComboBox
    Friend WithEvents Label2 As System.Windows.Forms.Label
    Friend WithEvents CheckedListBox1 As System.Windows.Forms.CheckedListBox
    Friend WithEvents Label4 As System.Windows.Forms.Label
    Friend WithEvents CreateCheckBox As System.Windows.Forms.CheckBox
    Friend WithEvents UpdateCheckBox As System.Windows.Forms.CheckBox
    Friend WithEvents ReportsCheckBox As System.Windows.Forms.CheckBox
    Friend WithEvents ReferenceCheckBox As System.Windows.Forms.CheckBox
    Friend WithEvents CreateLinkedCheckBox As System.Windows.Forms.CheckBox
    Friend WithEvents UpdateLinkedCheckBox As System.Windows.Forms.CheckBox
    Friend WithEvents Button1 As System.Windows.Forms.Button
    Public WithEvents DWHREPConCombo As System.Windows.Forms.ComboBox
    Friend WithEvents Select_All As System.Windows.Forms.Button
    Friend WithEvents Unselect_All As System.Windows.Forms.Button
    Friend WithEvents Button2 As System.Windows.Forms.Button
    Friend WithEvents usernameTextBox As System.Windows.Forms.TextBox
    Friend WithEvents Label5 As System.Windows.Forms.Label
    Friend WithEvents passwordTextBox As System.Windows.Forms.TextBox
    Friend WithEvents Label6 As System.Windows.Forms.Label
    Friend WithEvents Label7 As System.Windows.Forms.Label
    Friend WithEvents Label8 As System.Windows.Forms.Label
    Friend WithEvents Label9 As System.Windows.Forms.Label
    Friend WithEvents outputDirTextBox As System.Windows.Forms.TextBox
    Friend WithEvents Label10 As System.Windows.Forms.Label
    Friend WithEvents boVersionComboBox As System.Windows.Forms.ComboBox
    Friend WithEvents baseTechPackCombo As System.Windows.Forms.ComboBox
    Friend WithEvents FolderBrowserDialog1 As System.Windows.Forms.FolderBrowserDialog
    Friend WithEvents folderBrowserButton As System.Windows.Forms.Button
End Class
