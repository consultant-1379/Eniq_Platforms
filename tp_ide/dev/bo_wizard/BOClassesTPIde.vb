Option Strict Off

''
'  BOClasses class is a collection of BOClass classes
'
    Public NotInheritable Class BOClassesTPIde
        Private _classes As System.Collections.ArrayList = New System.Collections.ArrayList

        ''
        '  Gets count of BOClass classes in BOClasses class
        '
        ' @param Index Specifies the index in the BOClasses class
        ' @return Count of BOClass classes
        Public ReadOnly Property Count() As Integer
            Get
                If (Not _classes Is Nothing) Then
                    Return _classes.Count
                End If
                Return 0
            End Get
        End Property

        ''
        '  Gets BOClass class from BOClasses class based on given index.
        '
        ' @param Index Specifies the index in the BOClasses class
        ' @return Reference to BOClass
        Public ReadOnly Property Item(ByVal Index As Integer) As BOClass
            Get
                If (Index > 0) And (Index <= Me.Count) Then
                    Return CType(_classes.Item(Index - 1), BOClass)
                End If
                Return Nothing
            End Get
        End Property

        ''
        '  Adds BOClass class to BOClasses class
        '
        ' @param ValueIn Specifies reference to BOClass
        Public Sub AddItem(ByVal ValueIn As BOClass)

            If (Not _classes Is Nothing) Then
                _classes.Add(ValueIn)
            End If

        End Sub

        ''
        '  BOClass class defines classes in universe.
        '
        Public Class BOClass
            Private m_Name As String
            Private m_Description As String

        Private m_Classes As BOClassesTPIde
        Private m_Objects As BOObjectsTPIde
        Private m_Conditions As BOConditionsTPIde
            Private m_CounterClass As Boolean
            Private m_Joins As BOJoins

            ''
            ' Gets and sets value for Name parameter. 
            ' Name defines name.
            '
            ' @param Value Specifies value of Name parameter
            ' @return Value of Name parameter
            Public Property Name() As String
                Get
                    Name = m_Name
                End Get

                Set(ByVal Value As String)
                    m_Name = Value
                End Set

            End Property

            ''
            ' Gets and sets value for Description parameter. 
            ' Description defines description.
            '
            ' @param Value Specifies value of Description parameter
            ' @return Value of Description parameter
            Public Property Description() As String
                Get
                    Description = m_Description
                End Get

                Set(ByVal Value As String)
                    m_Description = Value
                End Set

            End Property

            ''
            ' Gets and sets value for Classes parameter. 
            ' Classes defines reference to class' sub-classes.
            '
            ' @param Value Specifies value of Classes parameter
            ' @return Value of Classes parameter
        Public Property Classes() As BOClassesTPIde
            Get
                Classes = m_Classes
            End Get

            Set(ByVal Value As BOClassesTPIde)
                m_Classes = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Objects parameter. 
        ' Objects defines reference to class' objects.
        '
        ' @param Value Specifies value of Objects parameter
        ' @return Value of Objects parameter
        Public Property Objects() As BOObjectsTPIde
            Get
                Objects = m_Objects
            End Get

            Set(ByVal Value As BOObjectsTPIde)
                m_Objects = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Conditions parameter. 
        ' Conditions defines reference to class' conditions.
        '
        ' @param Value Specifies value of Conditions parameter
        ' @return Value of Conditions parameter
        Public Property Conditions() As BOConditionsTPIde
            Get
                Conditions = m_Conditions
            End Get

            Set(ByVal Value As BOConditionsTPIde)
                m_Conditions = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Conditions parameter. 
        ' CondCounterClassitions defines whether class contains counters.
        '
        ' @param Value Specifies value of CounterClass parameter
        ' @return Value of CounterClass parameter
        Public Property CounterClass()
            Get
                CounterClass = m_CounterClass
            End Get

            Set(ByVal Value)
                m_CounterClass = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Joins parameter. 
        ' Joins defines reference to class' joins.
        '
        ' @param Value Specifies value of Joins parameter
        ' @return Value of Joins parameter
        Public Property Joins() As BOJoins
            Get
                Joins = m_Joins
            End Get

            Set(ByVal Value As BOJoins)
                m_Joins = Value
            End Set

        End Property



    End Class

    End Class