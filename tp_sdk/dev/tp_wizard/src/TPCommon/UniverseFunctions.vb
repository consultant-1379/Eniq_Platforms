Option Strict Off
Option Explicit On
Public Class UniverseFunctions 'require variables to be declared before being used


    Dim Conxt As Designer.Context
    Dim Jn As Designer.Join

    Dim RowNum As Integer
    Dim RowNum2 As Integer

    Dim DefaultKeyMaxAmount As String
    Dim DefaultCounterMaxAmount As String

    Dim TechPackName As String
    Dim VendorRelease As String
    Dim ProductNumber As String
    Dim TechPackType As String
    Dim TPVersion As String
    Dim TPReleaseVersion As String
    Dim TPDescription As String
    Dim ObjectBHSupport As Boolean
    Dim ElementBHSupport As Boolean
    Dim FullAware As Boolean


    Dim UniverseName As String
    Dim UniverseDescription As String

    Dim UniverseFileName As String

    Dim cust_univ_name As String
    Dim cust_univ_file_name As String

    Dim MeasurementTypes_RowCount As Short

    Dim original_mts As MeasurementTypes
    Dim mt As MeasurementTypes.MeasurementType

    Dim UnvMts As UniverseMeasurements
    Dim UnvMt As UniverseMeasurements.UniverseMeasurement

    Dim all_cnts As Counters
    Dim cnts As Counters
    Dim cnt As Counters.Counter

    Dim all_cnt_keys As CounterKeys
    Dim cnt_keys As CounterKeys
    Dim cnt_key As CounterKeys.CounterKey

    'Dim univ_joins As UnivJoins
    Dim univ_join As UnivJoins.UnivJoin
    Dim extra_joins As UnivJoins

    Dim univ_clss As UnivClasses
    Dim univ_cls As UnivClasses.UnivClass

    Dim RowCount As Integer
    Dim RowCount2 As Integer
    Dim mt_count As Integer
    Dim cnt_count As Long
    Dim cnt_key_count As Long

    Dim univ_cls_count As Long

    'Dim rts As ReferenceTypes
    Dim rt As ReferenceTypes.ReferenceType
    'Dim rds As ReferenceDatas
    Dim rd As ReferenceDatas.ReferenceData
    Dim rt_count As Long

    'Dim vector_rts As ReferenceTypes
    'Dim vector_rds As ReferenceDatas

    Dim dbCommand As System.Data.OleDb.OleDbCommand
    Dim dbReader As System.Data.OleDb.OleDbDataReader

    Dim tpAdoConn As String
    Dim baseAdoConn As String

    Dim tpConn As System.Data.OleDb.OleDbConnection
    Dim baseConn As System.Data.OleDb.OleDbConnection

    Dim bo_objects As BOObjects
    Dim bo_conditions As BOConditions

    Dim pub_keys As PublicKeys
    Dim pub_key As PublicKeys.PublicKey

    Dim counterParse As Boolean
    Dim joinParse As Boolean
    Dim referenceParse As Boolean
    Dim objectParse As Boolean
    Dim additionalObjectParse As Boolean
    Dim conditionParse As Boolean
    Dim additionalConditionParse As Boolean
    Dim integrityParse As Boolean

    Dim repobjs As ReportObjects
    Dim repobj As ReportObjects.ReportObject

    Dim repconds As ReportConditions
    Dim repcond As ReportConditions.ReportCondition

    Public Shared updatedTables As String
    Public Shared updatedClasses As String
    Public Shared updatedObjects As String
    Public Shared updatedConditions As String
    Public Shared updatedJoins As String
    Public Shared updatedContexts As String

    ''
    ' Adds counter objects to universe.
    '
    ' @param Univ Specifies reference to universe
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Private Function Universe_AddCounters(ByRef Univ As Object, ByRef CMTechPack As Boolean, ByRef logs As logMessages, ByRef mts As MeasurementTypes, ByRef vector_rds As ReferenceDatas) As Boolean

        Dim Cls As Designer.Class
        Dim Cls2 As Designer.Class
        Dim Cls3 As Designer.Class
        Dim Obj As Designer.Object
        'Dim Obj2 As Designer.Object
        'Dim Tbl As Designer.Table
        Dim ClassTree() As String
        Dim TreeCount As Integer
        Dim HierarchyRootClass As String
        Dim UsedClass As String
        Dim count As Integer
        Dim vectorTable As String


        Dim bo_objects As New BOObjects
        Dim univ_classes As New UnivClasses

        Dim ObjNum As Designer.Object

        For mt_count = 1 To mts.Count

            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" AndAlso mt.RankTable = False Then
                If CMTechPack = True Then
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass("Parameters"), mt.MeasurementTypeClassDescription & " Parameters", mt.MeasurementTypeClassDescription & " Parameters")
                Else
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass("Counters"), mt.MeasurementTypeClassDescription & " Counters", mt.MeasurementTypeClassDescription & " Counters")
                End If
                If mt.RankTable = False AndAlso mt.CreateCountTable = True Then 'COUNT tables
                    Cls2 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID, mt.Description)
                    Cls3 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID & "_RAW", mt.Description)

                    cnts = mt.Counters
                    For cnt_count = 1 To cnts.Count
                        cnt = cnts.Item(cnt_count)

                        Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)
                        Cls3 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW")

                        If cnt.UnivObject <> "" Then
                            UsedClass = ""
                            HierarchyRootClass = mt.MeasurementTypeID
                            If cnt.UnivClass <> "" Then
                                UsedClass = cnt.UnivClass
                                ClassTree = Split(cnt.UnivClass, "//")
                                For TreeCount = 0 To UBound(ClassTree)

                                    Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                    HierarchyRootClass = ClassTree(TreeCount)
                                    UsedClass = ClassTree(TreeCount)
                                Next TreeCount
                            End If

                            If UsedClass <> "" Then
                                Cls3 = univ_classes.addClass(Univ, Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                            End If

                            If cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue <> "NONE" AndAlso cnt.oneAggrValue <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                If cnt.oneAggrValue <> "SUM" Then
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                Else
                                    Obj = bo_objects.addObject(Cls2, cnt, True, cnt.oneAggrValue, logs)
                                End If
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName + ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                                If cnt.oneAggrValue <> "SUM" Then
                                    Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                Else
                                    Obj = bo_objects.addObject(Cls3, cnt, True, cnt.oneAggrValue, logs)
                                End If
                                If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                    Return False
                                End If
                            ElseIf cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ",DC." + cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + ")", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." + cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + ")", logs) = False Then
                                        Return False
                                    End If
                                End If

                                Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, "DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName, logs) = False Then
                                    Return False
                                End If
                            ElseIf cnt.oneAggrFormula = False Then 'If Time Aggregation and Group Aggregation are different
                                For count = 0 To UBound(cnt.Aggregations)
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count), logs)
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName & "))", logs) = False Then
                                            Return False
                                        End If
                                    Else
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName & "))", logs) = False Then
                                            Return False
                                        End If
                                    End If

                                    Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count), logs)
                                    If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                        Return False
                                    End If
                                Next count
                            Else
                                logs.AddLogText("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.MeasurementTypeID & "' not added/modified")
                            End If
                        Else

                        End If

                        'Vector objects here
                        If cnt.CounterType = "VECTOR" Then
                            vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                            For count = 1 To vector_rds.Count
                                rd = vector_rds.Item(count)
                                If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                    Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)
                                    Cls3 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW")

                                    UsedClass = ""
                                    HierarchyRootClass = mt.MeasurementTypeID
                                    If cnt.UnivClass <> "" Then
                                        UsedClass = cnt.UnivClass
                                        ClassTree = Split(cnt.UnivClass, "//")
                                        For TreeCount = 0 To UBound(ClassTree)
                                            Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                            HierarchyRootClass = ClassTree(TreeCount)
                                            UsedClass = ClassTree(TreeCount)
                                        Next TreeCount
                                    End If

                                    If UsedClass <> "" Then
                                        Cls3 = univ_classes.addClass(Univ, Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                                    End If

                                    Obj = bo_objects.addObject(Cls2, rd, False, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                        Return False
                                    End If
                                    Obj = bo_objects.addObject(Cls3, rd, False, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls3, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                        Return False
                                    End If
                                    'parseCounterObject(bo_objects, Obj, Cls3)
                                End If
                            Next count
                        End If
                        'Vector
                    Next cnt_count

                End If
                If mt.RankTable = False AndAlso mt.CreateCountTable = False Then 'Regular tables
                    Cls2 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID, mt.Description)

                    cnts = mt.Counters
                    For cnt_count = 1 To cnts.Count
                        cnt = cnts.Item(cnt_count)

                        Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)

                        If cnt.UnivObject <> "" Then

                            HierarchyRootClass = mt.MeasurementTypeID
                            If cnt.UnivClass <> "" Then
                                UsedClass = cnt.UnivClass
                                ClassTree = Split(cnt.UnivClass, "//")
                                For TreeCount = 0 To UBound(ClassTree)
                                    Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                    HierarchyRootClass = ClassTree(TreeCount)
                                    UsedClass = ClassTree(TreeCount)
                                Next TreeCount
                            End If

                            If cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue <> "NONE" AndAlso cnt.oneAggrValue <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                If cnt.oneAggrValue <> "SUM" Then
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                Else
                                    Obj = bo_objects.addObject(Cls2, cnt, True, cnt.oneAggrValue, logs)
                                End If
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                            ElseIf cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                If mt.PlainTable = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "DC." & cnt.MeasurementTypeID & "." & cnt.CounterName, logs) = False Then
                                        Return False
                                    End If
                                ElseIf CMTechPack = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName, logs) = False Then
                                        Return False
                                    End If
                                Else
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                            Return False
                                        End If
                                    Else
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                            Return False
                                        End If
                                    End If
                                End If
                            ElseIf cnt.oneAggrFormula = False Then 'If Time Aggregation and Group Aggregation are different
                                For count = 0 To UBound(cnt.Aggregations)
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count), logs)
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                            Return False
                                        End If
                                    Else
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                            Return False
                                        End If
                                    End If
                                Next count
                            Else
                                logs.AddLogText("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.MeasurementTypeID & "' not added/modified")
                            End If
                        Else
                        End If

                        'Vector objects here
                        If cnt.CounterType = "VECTOR" Then
                            vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                            For count = 1 To vector_rds.Count
                                rd = vector_rds.Item(count)
                                If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                    Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)

                                    HierarchyRootClass = mt.MeasurementTypeID
                                    If cnt.UnivClass <> "" Then
                                        UsedClass = cnt.UnivClass
                                        ClassTree = Split(cnt.UnivClass, "//")
                                        For TreeCount = 0 To UBound(ClassTree)
                                            Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                            HierarchyRootClass = ClassTree(TreeCount)
                                            UsedClass = ClassTree(TreeCount)
                                        Next TreeCount
                                    End If
                                    Obj = bo_objects.addObject(Cls2, rd, False, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                        Return False
                                    End If
                                End If
                            Next count
                        End If
                        'Vector
                    Next cnt_count

                End If
                'BH-classes
                If FullAware = False Then
                    If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = False Then
                        Cls2 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID & "_BH", mt.Description)

                        cnts = mt.Counters
                        For cnt_count = 1 To cnts.Count
                            cnt = cnts.Item(cnt_count)

                            Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_BH")

                            If cnt.UnivObject <> "" Then

                                HierarchyRootClass = mt.MeasurementTypeID
                                If cnt.UnivClass <> "" Then
                                    UsedClass = cnt.UnivClass
                                    ClassTree = Split(cnt.UnivClass, "//")
                                    For TreeCount = 0 To UBound(ClassTree)
                                        Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass & "_BH"), ClassTree(TreeCount) & "_BH", ClassTree(TreeCount) & "_BH")
                                        HierarchyRootClass = ClassTree(TreeCount)
                                        UsedClass = ClassTree(TreeCount)
                                    Next TreeCount
                                End If

                                If cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue <> "NONE" AndAlso cnt.oneAggrValue <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                    If cnt.oneAggrValue <> "SUM" Then
                                        Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                    Else
                                        Obj = bo_objects.addObject(Cls2, cnt, True, cnt.oneAggrValue, logs)
                                    End If
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, LCase(cnt.oneAggrValue) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")", logs) = False Then
                                        Return False
                                    End If
                                ElseIf cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue, logs)
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName, logs) = False Then
                                        Return False
                                    End If
                                ElseIf cnt.oneAggrFormula = False Then 'If Time Aggregation and Group Aggregation are different
                                    For count = 0 To UBound(cnt.Aggregations)
                                        Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count), logs)
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, LCase(cnt.Aggregations(count)) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")", logs) = False Then
                                            Return False
                                        End If
                                    Next count
                                Else
                                    logs.AddLogText("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.MeasurementTypeID & "' not added/modified")
                                End If
                            Else
                            End If

                            'Vector objects here
                            If cnt.CounterType = "VECTOR" Then
                                vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                                For count = 1 To vector_rds.Count
                                    rd = vector_rds.Item(count)
                                    If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                        Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_BH")

                                        HierarchyRootClass = mt.MeasurementTypeID
                                        If rd.UnivClass <> "" Then
                                            UsedClass = rd.UnivClass
                                            ClassTree = Split(rd.UnivClass, "//")
                                            For TreeCount = 0 To UBound(ClassTree)
                                                Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass & "_BH"), ClassTree(TreeCount) & "_BH", ClassTree(TreeCount) & "_BH")
                                                HierarchyRootClass = ClassTree(TreeCount)
                                                UsedClass = ClassTree(TreeCount)
                                            Next TreeCount
                                        End If
                                        Obj = bo_objects.addObject(Cls2, rd, False, logs)
                                        If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                            Return False
                                        End If
                                    End If
                                Next count
                            End If
                            'Vector
                        Next cnt_count
                    End If
                End If
            End If
        Next mt_count
        Return True

    End Function
    Function parseCounterObject(ByRef objects As BOObjects, ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, ByRef CMTechPack As Boolean, ByRef SelectClause As String, ByRef logs As logMessages) As Boolean
        Dim result As Integer
        Obj.Select = SelectClause
        Obj.Format.NumberFormat = objects.formatCounterObject(Obj, CMTechPack)
        If counterParse = True Then
            result = objects.ParseCounterObject(Obj, Cls, logs)
            If result = 0 Then
                counterParse = True
                Return True
            End If
            If result = 1 Then
                counterParse = False
                Return True
            End If
            If result = 2 Then
                Return False
            End If
        End If
        Return True
    End Function
    Function parseReferenceObject(ByRef objects As BOObjects, ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, ByRef SelectClause As String, ByRef logs As logMessages) As Boolean
        Dim result As Integer
        Obj.Select = SelectClause
        If referenceParse = True Then
            result = objects.ParseReferenceObject(Obj, Cls, logs)
            If result = 0 Then
                referenceParse = True
                Return True
            End If
            If result = 1 Then
                referenceParse = False
                Return True
            End If
            If result = 2 Then
                Return False
            End If
        End If
        Return True
    End Function

    ''
    ' Adds counter key objects to universe.
    '
    ' @param Univ Specifies reference to universe
    Function Universe_AddCounterKeys(ByRef Univ As Designer.Universe, ByRef logs As logMessages, ByRef mts As MeasurementTypes) As Boolean

        Dim Cls As Designer.Class
        Dim Cls2 As Designer.Class
        Dim Obj As Designer.Object
        Dim selectClause As String
        Dim count As Integer

        Dim bo_objects As New BOObjects
        Dim univ_classes As New UnivClasses

        For mt_count = 1 To mts.Count

            mt = mts.Item(mt_count)
            cnt_keys = mt.CounterKeys
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = False Then
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.MeasurementTypeID), mt.MeasurementTypeID & "_Keys", "Keys for measurement " & mt.MeasurementTypeID)
                    For cnt_key_count = 1 To cnt_keys.Count
                        cnt_key = cnt_keys.Item(cnt_key_count)
                        If cnt_key.UnivObject <> "" Then
                            Try
                                Obj = Cls.Objects.Item(cnt_key.UnivObject)
                            Catch e As Exception
                                Obj = Cls.Objects.Add(cnt_key.UnivObject, Cls)
                            End Try
                            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                            If mt.PlainTable = True Then
                                selectClause = "DC." & cnt_key.MeasurementTypeID & "." & cnt_key.CounterKeyName
                            End If
                            If mt.DayAggregation = False AndAlso mt.PlainTable = False Then
                                selectClause = "DC." & cnt_key.MeasurementTypeID & "_RAW." & cnt_key.CounterKeyName
                            End If
                            'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.MeasurementTypeID & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.MeasurementTypeID & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.MeasurementTypeID & "_RAW." & cnt_key.CounterKeyName & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.MeasurementTypeID & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.MeasurementTypeID & "_RAW." & cnt_key.CounterKeyName & ")"
                                End If
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.MeasurementTypeID & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.MeasurementTypeID & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.MeasurementTypeID & "_COUNT." & cnt_key.CounterKeyName & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.MeasurementTypeID & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.MeasurementTypeID & "_COUNT." & cnt_key.CounterKeyName & ")"
                                End If
                            End If

                            bo_objects.keyFormat(Obj, cnt_key, logs)
                            If parseReferenceObject(bo_objects, Obj, Cls, selectClause, logs) = False Then
                                Return False
                            End If

                            If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                Cls2 = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.MeasurementTypeID & "_RAW"), mt.MeasurementTypeID & "_RAW_Keys", "Keys for measurement " & mt.MeasurementTypeID)
                                Try
                                    Obj = Cls2.Objects.Item(cnt_key.UnivObject)
                                Catch e As Exception
                                    Obj = Cls2.Objects.Add(cnt_key.UnivObject, Cls2)
                                End Try
                                UniverseFunctions.updatedObjects &= Cls2.Name & "/" & Obj.Name & ";"
                                bo_objects.keyFormat(Obj, cnt_key, logs)
                                If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & cnt_key.MeasurementTypeID & "_RAW." & cnt_key.CounterKeyName, logs) = False Then
                                    Return False
                                End If

                            End If
                        End If
                    Next cnt_key_count
                    'Vector Index object
                    For count = 1 To mt.Counters.Count
                        If mt.Counters.Item(count).CounterType = "VECTOR" Then

                            '  Try  /// JTS MIKSI KILAHTI MAGICin kamoilla?
                            ' Obj = Cls2.Objects.Item("Vector Index")
                            'Catch e As Exception
                            '   Obj = Cls2.Objects.Add("Vector Index", Cls2)
                            '  End Try

                            ' Try
                            'cnt_key.CounterKeyName = "Vector"
                            'UniverseFunctions.updatedObjects &= Cls2.Name & "/" & Obj.Name & ";"
                            'bo_objects.keyFormat(Obj, cnt_key, logs)

                            'selectClause = "DC." & mt.MeasurementTypeID & "_RAW." & "DCVECTOR_INDEX"
                            'Obj.Description = "Vector Index"
                            'bo_objects.setObjectType("integer", Obj, False, logs)
                            'Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                            'Obj.Format.NumberFormat = bo_objects.formatObject(Obj)
                            'Obj.HasListOfValues = True
                            'Obj.AllowUserToEditLov = True
                            'Obj.AutomaticLovRefreshBeforeUse = False
                            'Obj.ExportLovWithUniverse = False
                            'If parseReferenceObject(bo_objects, Obj, Cls2, selectClause, logs) = False Then
                            '     Return False
                            'End If

                            ' Catch ex As Exception
                            ' logs.AddLogText("Counter Key Error: " & ex.Message)

                            'End Try

                            Try
                                Obj = Cls.Objects.Item("Vector Index")
                            Catch e As Exception
                                Obj = Cls.Objects.Add("Vector Index", Cls)
                            End Try
                            UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                            If mt.PlainTable = True Then
                                selectClause = "DC." & mt.MeasurementTypeID & "." & "DCVECTOR_INDEX"
                            End If
                            If mt.DayAggregation = False AndAlso mt.PlainTable = False Then
                                selectClause = "DC." & mt.MeasurementTypeID & "_RAW." & "DCVECTOR_INDEX"
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.MeasurementTypeID & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.MeasurementTypeID & "_RAW." & "DCVECTOR_INDEX" & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.MeasurementTypeID & "_RAW." & "DCVECTOR_INDEX" & ")"
                                End If
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.MeasurementTypeID & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.MeasurementTypeID & "_COUNT." & "DCVECTOR_INDEX" & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.MeasurementTypeID & "_COUNT." & "DCVECTOR_INDEX" & ")"
                                End If
                            End If

                            Try
                                Obj.Description = "Vector Index"
                                bo_objects.setObjectType("integer", Obj, False, logs)
                                Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                                Obj.Format.NumberFormat = bo_objects.formatObject(Obj)
                                Obj.HasListOfValues = True
                                Obj.AllowUserToEditLov = True
                                Obj.AutomaticLovRefreshBeforeUse = False
                                Obj.ExportLovWithUniverse = False
                                If parseReferenceObject(bo_objects, Obj, Cls, selectClause, logs) = False Then
                                    Return False
                                End If
                            Catch ex As Exception
                                logs.AddLogText("Counter Key Error: " & ex.Message)
                            End Try

                            Exit For
                        End If
                    Next count

                    If FullAware = False Then
                        If mt.ObjectBusyHours <> "" Then
                            Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.MeasurementTypeID + "_BH"), mt.MeasurementTypeID + "_BH_Keys", "Keys for measurement " & mt.MeasurementTypeID & " for busy hours")

                            For cnt_key_count = 1 To cnt_keys.Count
                                cnt_key = cnt_keys.Item(cnt_key_count)
                                If cnt_key.UnivObject <> "" Then
                                    Try
                                        Obj = Cls.Objects.Item(cnt_key.UnivObject)
                                    Catch e As Exception
                                        Obj = Cls.Objects.Add(cnt_key.UnivObject, Cls)
                                    End Try
                                    UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    bo_objects.keyFormat(Obj, cnt_key, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls, "DC." & cnt_key.MeasurementTypeID & "_DAYBH." & cnt_key.CounterKeyName, logs) = False Then
                                        Return False
                                    End If

                                End If
                            Next cnt_key_count

                            'Vector Index object
                            For count = 1 To mt.Counters.Count
                                If mt.Counters.Item(count).CounterType = "VECTOR" Then
                                    Try
                                        Obj = Cls.Objects.Item("Vector Index")
                                    Catch e As Exception
                                        Obj = Cls.Objects.Add("Vector Index", Cls)
                                    End Try
                                    UniverseFunctions.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    Try
                                        Obj.Description = "Vector Index"
                                        bo_objects.setObjectType("integer", Obj, False, logs)
                                        Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                                        Obj.Format.NumberFormat = bo_objects.formatObject(Obj)
                                        Obj.HasListOfValues = True
                                        Obj.AllowUserToEditLov = True
                                        Obj.AutomaticLovRefreshBeforeUse = False
                                        Obj.ExportLovWithUniverse = False
                                        If parseReferenceObject(bo_objects, Obj, Cls, "DC." & mt.MeasurementTypeID & "_DAYBH." & "DCVECTOR_INDEX", logs) = False Then
                                            Return False
                                        End If
                                    Catch ex As Exception
                                        logs.AddLogText("Counter Key Error: " & ex.Message)
                                    End Try
                                    Exit For
                                End If
                            Next count
                        End If
                    End If
                End If
            End If
        Next mt_count
        Return True

    End Function


    ''
    ' Adds classes to universe.
    '
    ' @param Univ Specifies reference to universe
    Private Function Universe_AddClasses(ByRef Univ As Object, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef NewUniverse As Boolean, ByRef logs As logMessages, ByRef UniverseNameExtension As String) As Boolean
        Dim Cls As Designer.Class
        Dim addClass As Boolean

        For univ_cls_count = 1 To univ_clss.Count
            univ_cls = univ_clss.Item(univ_cls_count)
            addClass = False

            If univ_cls.UniverseExtension = "all" Then
                addClass = True
            ElseIf univ_cls.UniverseExtension = "" AndAlso UniverseNameExtension = "" Then
                addClass = True
            Else
                Dim UniverseCountList() As String
                Dim UnvCount As Integer
                If InStrRev(univ_cls.UniverseExtension, ",") = 0 Then
                    If univ_cls.UniverseExtension = UniverseNameExtension Then
                        addClass = True
                    End If
                Else
                    UniverseCountList = Split(univ_cls.UniverseExtension, ",")
                    For UnvCount = 0 To UBound(UniverseCountList)
                        If UniverseCountList(UnvCount) = UniverseNameExtension Then
                            addClass = True
                            Exit For
                        End If
                    Next
                End If
            End If

            If addClass = True Then
                If univ_cls.ObjectBHRelated = ObjectBHSupport OrElse univ_cls.ElementBHRelated = ElementBHSupport OrElse (univ_cls.ObjectBHRelated = False AndAlso univ_cls.ElementBHRelated = False) Then
                    If univ_cls.ParentClassName = "Root" Then
                        If univ_clss.addRootClass(Univ, univ_cls, NewUniverse, logs) = False Then
                            Return False
                        End If
                    Else
                        If univ_clss.addChildClass(Univ, univ_cls, NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                ElseIf univ_cls.ObjectBHRelated = True AndAlso ObjectBHSupport = False AndAlso univ_cls.ElementBHRelated = True AndAlso ElementBHSupport = False Then
                    'Do nothing
                Else
                    If univ_cls.ParentClassName = "Root" Then
                        If univ_clss.addRootClass(Univ, univ_cls, NewUniverse, logs) = False Then
                            Return False
                        End If
                    Else
                        If univ_clss.addChildClass(Univ, univ_cls, NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                End If
            End If
        Next univ_cls_count
        Return True
    End Function

    ''
    ' Adds extra objects to universe.
    '
    ' @param Univ Specifies reference to universe
    ' @remarks Extra objects are defined in TP definition's sheet 'Universe objects'.
    Private Function Universe_AddObjects(ByRef Univ As Object, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef logs As logMessages, ByRef mts As MeasurementTypes, ByRef rds As ReferenceDatas, ByRef UniverseNameExtension As String) As Boolean
        Dim count As Integer

        bo_objects = New BOObjects
        bo_objects.ObjectParse = objectParse

        If bo_objects.addObjects(TechPackName, TPVersion, Univ, tpConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, logs, UniverseNameExtension) = False Then
            Return False
        End If
        If bo_objects.addObjects(TechPackName, TPVersion, Univ, baseConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, logs, UniverseNameExtension) = False Then
            Return False
        End If
        'reference objects
        For count = 1 To rds.Count
            rd = rds.Item(count)
            If rd.UnivObject <> "" AndAlso rd.UnivClass <> "" Then
                If bo_objects.addObject(Univ, rd.UnivClass, rd.UnivObject, rd.Datatype, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, rd.Description, logs) = False Then
                    Return False
                End If
            End If
        Next count
        Return True

    End Function


    ''
    ' Adds extra conditions to universe.
    '
    ' @param Univ Specifies reference to universe
    ' @remarks Extra conditions are defined in TP definition's sheet 'Universe conditions'.
    Private Function Universe_AddConditions(ByRef Univ As Designer.Universe, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef logs As logMessages, ByRef mts As MeasurementTypes, ByRef rds As ReferenceDatas, ByRef UniverseNameExtension As String) As Boolean
        Dim count As Integer

        bo_conditions = New BOConditions
        bo_conditions.ConditionParse = conditionParse

        If bo_conditions.addConditions(TechPackName, Univ, tpConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, logs, UniverseNameExtension) = False Then
            Return False
        End If
        If bo_conditions.addConditions(TechPackName, Univ, baseConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, logs, UniverseNameExtension) = False Then
            Return False
        End If
        'reference conditions
        For count = 1 To rds.Count
            rd = rds.Item(count)
            If rd.UnivObject <> "" AndAlso rd.UnivClass <> "" AndAlso rd.UnivCondition = True Then
                If bo_conditions.addCondition(Univ, rd.UnivClass, rd.UnivObject, rd.Description, logs) = False Then
                    Return False
                End If
            End If
        Next count

        Return True
    End Function

    ''
    ' Adds tables to universe. 
    '
    ' @param Univ Specifies reference to universe
    Private Function Universe_AddTables(ByRef Univ As Object, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef NewUniverse As Boolean, ByRef logs As logMessages, ByRef mts As MeasurementTypes, ByRef rts As ReferenceTypes, ByRef vector_rts As ReferenceTypes, ByRef UniverseNameExtension As String) As Boolean
        Dim Tbl As Designer.Table
        Dim bo_tables As New BOTables
        If bo_tables.addTables(Univ, tpConn, dbCommand, dbReader, ObjectBHSupport, ElementBHSupport, NewUniverse, logs, UniverseNameExtension) = False Then
            Return False
        End If
        If bo_tables.addTables(Univ, baseConn, dbCommand, dbReader, ObjectBHSupport, ElementBHSupport, NewUniverse, logs, UniverseNameExtension) = False Then
            Return False
        End If

        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            'If rt.IncludeInUniverse = True Then
            If bo_tables.addTable(Univ, rt.ReferenceTypeID, NewUniverse, logs) = False Then
                Return False
            End If
            'End If
        Next rt_count

        For rt_count = 1 To vector_rts.Count
            rt = vector_rts.Item(rt_count)
            'If rt.IncludeInUniverse = True Then
            If bo_tables.addTable(Univ, rt.ReferenceTypeID, NewUniverse, logs) = False Then
                Return False
            End If
            'End If
        Next rt_count

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = False Then
                    If mt.PlainTable = False Then
                        If bo_tables.addTable(Univ, mt.MeasurementTypeID & "_RAW", NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                    If mt.PlainTable = True Then
                        If bo_tables.addTable(Univ, mt.MeasurementTypeID, NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                    If mt.CreateCountTable = True Then
                        If bo_tables.addTable(Univ, mt.MeasurementTypeID & "_COUNT", NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                    If mt.DayAggregation = True Then
                        If bo_tables.addTable(Univ, mt.MeasurementTypeID & "_DAY", NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                    If mt.ObjectBusyHours <> "" Then
                        If bo_tables.addTable(Univ, mt.MeasurementTypeID & "_DAYBH", NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                Else
                    If mt.ElementBusyHours = True Then
                        If bo_tables.addTable(Univ, mt.MeasurementTypeID & "_RANKBH", NewUniverse, logs) = False Then
                            Return False
                        End If
                    End If
                End If
            End If
        Next mt_count

        Return True

    End Function

    Private Function getObjectBHSupport(ByRef mts As MeasurementTypes) As Boolean

        Dim Found As Boolean
        Found = False
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = True Then
                    If mt.ObjectBusyHours <> "" Then
                        Found = True
                        Exit For
                    End If
                End If
            End If
        Next mt_count

        Return Found

    End Function

    Private Function getElementBHSupport(ByRef mts As MeasurementTypes) As Boolean

        Dim Found As Boolean
        Found = False
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = True Then
                    If mt.ElementBusyHours = True Then
                        Found = True
                        Exit For
                    End If
                End If
            End If
        Next mt_count

        Return Found

    End Function

    ''
    ' Adds incompatible objects and conditions to universe. 
    ' Objects are: 
    ' - Time/Hour to DAY measurement tables
    ' - Element (Busy Hour)/Element Name to DAY measurement tables
    ' Conditions are: 
    ' - Element (Busy Hour)/Select Element Name to DAY measurement tables
    '
    ' @param Univ Specifies reference to universe
    Private Sub Universe_AddIncompatibleObjects(ByRef Univ As Designer.Universe, ByRef logs As logMessages, ByRef mts As MeasurementTypes)

        Dim Obj As Designer.Object

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False AndAlso mt.DayAggregation = True Then
                Try
                    Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatibleObjects.Item("Hour")
                Catch e As Exception
                    Try
                        Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatibleObjects.Add("Hour", "Time")
                    Catch ex As Exception
                        logs.AddLogText("Incompatible Object 'Hour/Time' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAY" & "'.")
                        logs.AddLogText("Incompatible Object exception: " & ex.ToString)
                    End Try
                End Try
                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                    Try
                        Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAYBH").IncompatibleObjects.Item("Hour")
                    Catch e As Exception
                        Try
                            Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAYBH").IncompatibleObjects.Add("Hour", "Time")
                        Catch ex As Exception
                            logs.AddLogText("Incompatible Object 'Hour/Time' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAYBH" & "'.")
                            logs.AddLogText("Incompatible Object exception: " & ex.ToString)
                        End Try
                    End Try
                    Try
                        Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatibleObjects.Item("Busy Hour")
                    Catch e As Exception
                        Try
                            Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatibleObjects.Add("Busy Hour", "Busy Hour")
                        Catch ex As Exception
                            logs.AddLogText("Incompatible Object 'Busy Hour/Busy Hour' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAY" & "'.")
                            logs.AddLogText("Incompatible Object exception: " & ex.ToString)
                        End Try
                    End Try
                End If
            End If
            If mt.RankTable = False AndAlso mt.ElementBusyHours = True Then
                Try
                    Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatibleObjects.Item("Element Name")
                Catch e As Exception
                    Try
                        Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatibleObjects.Add("Element Name", "Element (Busy Hour)")
                    Catch ex As Exception
                        logs.AddLogText("Incompatible Object 'Element (Busy Hour)/Element Name' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAY" & "'.")
                        logs.AddLogText("Incompatible Object exception: " & ex.ToString)
                    End Try
                End Try
                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                    Try
                        Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAYBH").IncompatibleObjects.Item("Element Name")
                    Catch e As Exception
                        Try
                            Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAYBH").IncompatibleObjects.Add("Element Name", "Element (Busy Hour)")
                        Catch ex As Exception
                            logs.AddLogText("Incompatible Object 'Element (Busy Hour)/Element Name' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAYBH" & "'.")
                            logs.AddLogText("Incompatible Object exception: " & ex.ToString)
                        End Try
                    End Try
                End If
                Try
                    Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatiblePredefConditions.Item("Select Element Name")
                Catch e As Exception
                    Try
                        Try
                            Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatiblePredefConditions.Add("Select Element Name", "Element (Busy Hour)")
                        Catch ex As Exception
                            logs.AddLogText("Incompatible Condition 'Element (Busy Hour)/Select Element Name' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAY" & "'.")
                            logs.AddLogText("Incompatible Condition exception: " & ex.ToString)
                        End Try
                    Catch ex As Exception
                        Try
                            Univ.Tables("DC." & mt.MeasurementTypeID & "_DAY").IncompatiblePredefConditions.Add("Select Element Name")
                        Catch ex2 As Exception
                            logs.AddLogText("Incompatible Condition 'Select Element Name' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAY" & "'.")
                            logs.AddLogText("Incompatible Condition exception: " & ex2.ToString)
                        End Try
                    End Try
                End Try
                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                    Try
                        Obj = Univ.Tables("DC." & mt.MeasurementTypeID & "_DAYBH").IncompatiblePredefConditions.Item("Select Element Name")
                    Catch e As Exception
                        Try
                            Try
                                Univ.Tables("DC." & mt.MeasurementTypeID & "_DAYBH").IncompatiblePredefConditions.Add("Select Element Name", "Element (Busy Hour)")
                            Catch ex As Exception
                                logs.AddLogText("Incompatible Condition 'Element (Busy Hour)/Select Element Name' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAYBH" & "'.")
                                logs.AddLogText("Incompatible Condition exception: " & ex.ToString)
                            End Try
                        Catch ex As Exception
                            Try
                                Univ.Tables("DC." & mt.MeasurementTypeID & "_DAYBH").IncompatiblePredefConditions.Add("Select Element Name")
                            Catch ex2 As Exception
                                logs.AddLogText("Incompatible Condition 'Select Element Name' adding failed for Table'" & "DC." & mt.MeasurementTypeID & "_DAYBH" & "'.")
                                logs.AddLogText("Incompatible Condition exception: " & ex2.ToString)
                            End Try
                        End Try
                    End Try
                End If
            End If
        Next mt_count


    End Sub
    Private Function Universe_RemoveContexts(ByRef Univ As Designer.Universe, ByRef logs As logMessages) As String

        Dim Jn As Designer.Join
        Dim Cntxt As Designer.Context

        Dim extraJoin As UnivJoins.UnivJoin

        'log extra joins

        'For Each Cntxt In Univ.Contexts
        'Try
        'For Each Jn In Univ.Contexts(Cntxt.Name).Joins
        'extraJoin = New UnivJoins.UnivJoin
        'extraJoin.Expression = Jn.Expression
        'extraJoin.Contexts = Cntxt.Name
        'extraJoin.Cardinality = setCardinality(Jn)
        'extra_joins.AddItem(extraJoin)
        'Next Jn
        'Catch ex As Exception
        'logs.AddLogText("Context '" & Cntxt.Name & "' join marking failed. Error: " & ex.Message)
        'End Try
        'Next Cntxt


        'remove contexts
        For Each Cntxt In Univ.Contexts
            Cntxt.Delete()
        Next Cntxt

        'remove joins
        For Each Jn In Univ.Joins
            Jn.Delete()
        Next Jn

    End Function
    Function setCardinality(ByRef Jn As Designer.Join) As String
        If Jn.Cardinality = Designer.DsCardinality.dsManyToOneCardinality Then
            Return "n_to_1"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsOneToManyCardinality Then
            Return "1_to_n"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsOneToOneCardinality Then
            Return "1_to_1"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsManyToManyCardinality Then
            Return "n_to_n"
        End If
    End Function
    ''
    ' Adds joins to universe.
    '
    ' @param Univ Specifies reference to universe
    Private Function Universe_AddJoins(ByRef Univ As Designer.Universe, ByRef logs As logMessages, ByRef mts As MeasurementTypes, ByRef univ_joins As UnivJoins) As Boolean
        Dim Jn As Designer.Join
        Dim JoinCount As Integer
        Dim Result As MsgBoxResult

        For JoinCount = 1 To univ_joins.Count
            univ_join = univ_joins.Item(JoinCount)
            Try
                Jn = Univ.Joins.Item(univ_join.Expression)
                UniverseFunctions.updatedJoins &= Jn.Expression & ";"
            Catch ex As Exception
                Try
                    Jn = Univ.Joins.Add(univ_join.Expression)
                    UniverseFunctions.updatedJoins &= Jn.Expression & ";"
                Catch e As Exception
                    logs.AddLogText("Join Adding failed for '" & univ_join.Expression & "'.")
                    logs.AddLogText("Join Adding Exception: " & e.ToString)
                End Try
            End Try
            Jn.Cardinality = univ_join.Cardinality
            If joinParse = True Then
                Try
                    Jn.Parse()
                Catch ex As Exception
                    logs.AddLogText("Join Parse Exception: " & ex.ToString)
                End Try
            End If
        Next JoinCount

        'add obsolete joins
        For JoinCount = 1 To extra_joins.Count
            univ_join = extra_joins.Item(JoinCount)
            Try
                Jn = Univ.Joins.Item(univ_join.Expression)
            Catch ex As Exception
                Try
                    Jn = Univ.Joins.Add(univ_join.Expression)
                Catch e As Exception
                    logs.AddLogText("Join Adding failed for '" & univ_join.Expression & "'.")
                    logs.AddLogText("Join Adding Exception: " & e.ToString)
                End Try
            End Try
            Jn.Cardinality = univ_join.Cardinality
            If joinParse = True Then
                Try
                    Jn.Parse()
                Catch ex As Exception
                    logs.AddLogText("Join Parse Exception: " & ex.ToString)
                End Try
            End If
        Next JoinCount

        Universe_BuildContexts(Univ, logs, mts, univ_joins)

        Return True
    End Function
    ''
    ' Adds contexts to universe. 
    '
    ' @param Univ Specifies reference to universe
    Private Sub Universe_BuildContexts(ByRef Univ As Designer.Universe, ByRef logs As logMessages, ByRef mts As MeasurementTypes, ByRef univ_joins As UnivJoins)

        Dim JoinContexts() As String
        Dim ExcludedJoinContexts() As String
        Dim ContextCount As Short
        Dim ExcludedCount As Short
        Dim JnFound As Boolean
        Dim JoinCount As Integer

        Dim bo_contexts As New BOContexts

        JnFound = False

        For mt_count = 1 To mts.Count

            mt = mts.Item(mt_count)

            If mt.RankTable = False Then
                If mt.PlainTable = True Then
                    Conxt = bo_contexts.addContext(Univ, mt.MeasurementTypeID)
                Else
                    Conxt = bo_contexts.addContext(Univ, mt.MeasurementTypeID + "_RAW")
                End If
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If mt.PlainTable = True Then
                        If InStrRev(univ_join.Expression, mt.MeasurementTypeID + ".") > 0 Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        End If
                    Else
                        If InStrRev(univ_join.Expression, mt.MeasurementTypeID + "_RAW.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") = 0 AndAlso InStrRev(univ_join.Expression, "ELEM_RANKBH.") = 0 Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        End If
                    End If
                Next JoinCount
            End If

            If mt.RankTable = False AndAlso mt.CreateCountTable = True Then
                Conxt = bo_contexts.addContext(Univ, mt.MeasurementTypeID + "_COUNT")
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If InStrRev(univ_join.Expression, mt.MeasurementTypeID & "_COUNT.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") = 0 AndAlso InStrRev(univ_join.Expression, "ELEM_RANKBH.") = 0 Then
                        AddJoinToContext(Conxt, univ_join, logs)
                    End If
                Next JoinCount
            End If
            If mt.DayAggregation = True AndAlso mt.RankTable = False Then
                Conxt = bo_contexts.addContext(Univ, mt.MeasurementTypeID + "_DAY")
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If InStrRev(univ_join.Expression, mt.MeasurementTypeID & "_DAY.") > 0 Then
                        AddJoinToContext(Conxt, univ_join, logs)
                    End If
                Next JoinCount
            End If

            If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = False Then
                Conxt = bo_contexts.addContext(Univ, mt.MeasurementTypeID + "_DAYBH")
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If InStrRev(univ_join.Expression, mt.MeasurementTypeID & "_DAYBH.") > 0 Then
                        AddJoinToContext(Conxt, univ_join, logs)
                    End If
                Next JoinCount
            End If

            If mt.ElementBusyHours = True AndAlso mt.RankTable = False Then
                Conxt = bo_contexts.addContext(Univ, mt.MeasurementTypeID + "_ELEMBH")
                ' Add Element Joins
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If mt.CreateCountTable = True Then
                        If InStrRev(univ_join.Expression, mt.MeasurementTypeID & "_COUNT.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") > 0 Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        ElseIf InStrRev(univ_join.Expression, mt.MeasurementTypeID & "_COUNT.") > 0 AndAlso univ_join.Contexts = "ELEMBH" Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        ElseIf InStrRev(univ_join.Expression, "_COUNT.") = 0 AndAlso InStrRev(univ_join.Expression, "_RAW.") = 0 AndAlso univ_join.Contexts = "ELEMBH" Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        Else
                            'do nothing
                        End If
                    Else
                        If InStrRev(univ_join.Expression, mt.MeasurementTypeID & "_RAW.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") > 0 Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        ElseIf InStrRev(univ_join.Expression, mt.MeasurementTypeID & "_RAW.") > 0 AndAlso univ_join.Contexts = "ELEMBH" Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        ElseIf InStrRev(univ_join.Expression, "_RAW.") = 0 AndAlso InStrRev(univ_join.Expression, "_COUNT.") = 0 AndAlso univ_join.Contexts = "ELEMBH" Then
                            AddJoinToContext(Conxt, univ_join, logs)
                        Else
                            'do nothing
                        End If
                    End If

                Next JoinCount

            End If

        Next mt_count

        Dim context_location As Integer
        For Each Conxt In Univ.Contexts
            For JoinCount = 1 To univ_joins.Count
                univ_join = univ_joins.Item(JoinCount)
                'all contexts
                If univ_join.Contexts = "All" Then
                    AddJoinToContext(Conxt, univ_join, logs)

                ElseIf univ_join.Contexts <> "All" AndAlso univ_join.Contexts <> "" AndAlso univ_join.Contexts <> "ELEMBH" Then
                    JoinContexts = Split(univ_join.Contexts, ",")

                    For ContextCount = 0 To UBound(JoinContexts)
                        context_location = InStrRev(Conxt.Name, JoinContexts(ContextCount) & "_")
                        If context_location > 0 Then
                            If InStrRev(univ_join.Expression, Conxt.Name.Substring(0, context_location)) > 0 Then

                                If univ_join.ExcludedContexts <> "" Then
                                    ExcludedJoinContexts = Split(univ_join.ExcludedContexts, ",")
                                    For ExcludedCount = 0 To UBound(ExcludedJoinContexts)
                                        If InStrRev(Conxt.Name, ExcludedJoinContexts(ExcludedCount)) = 0 Then
                                            AddJoinToContext(Conxt, univ_join, logs)
                                        End If
                                    Next ExcludedCount
                                Else
                                    AddJoinToContext(Conxt, univ_join, logs)
                                End If
                            End If
                        End If
                    Next ContextCount
                Else
                End If
            Next JoinCount
        Next Conxt


        'obsolete contexts
        'For Each Conxt In Univ.Contexts
        'For JoinCount = 1 To extra_joins.Count
        'univ_join = extra_joins.Item(JoinCount)
        'all contexts
        'If univ_join.Contexts <> "" Then
        'If univ_join.Contexts = Conxt.Name Then
        'AddJoinToContext(Conxt, univ_join, logs)
        'End If
        'End If
        'Next JoinCount
        'Next Conxt

    End Sub
    ''
    ' Adds join to a context. 
    '
    ' @param Conxt Specifies reference to contexts
    ' @param univ_join Specifies reference to universe join
    Private Sub AddJoinToContext(ByRef Conxt As Designer.Context, ByRef univ_join As UnivJoins.UnivJoin, ByRef logs As logMessages)
        Dim JnFound As Boolean
        JnFound = False
        Dim count As Integer
        If Not univ_join.ExcludedContexts <> "" AndAlso InStrRev(Conxt.Name, univ_join.ExcludedContexts) > 0 Then
            Try
                For Each Jn In Conxt.Joins
                    If Jn.Expression = univ_join.Expression Then
                        JnFound = True
                        Exit For
                    End If
                Next
            Catch ex As Exception
                'logs.AddLogText("Context '" & Conxt.Name & "' join: " & univ_join.Expression & ". Error: " & ex.Message)
            End Try
            If JnFound = False Then
                Try
                    Jn = Conxt.Joins.Add(univ_join.Expression)
                Catch e As Exception
                    logs.AddLogText("Adding join '" & univ_join.Expression & "' to context '" & Conxt.Name & "' fails. Check if joins exists for context already.")
                End Try
            End If
        End If
    End Sub

    ''
    ' Adds additional object and conditions to universe.
    ' Objects are:
    ' - data_coverage
    ' - period_duration
    ' - datetime (raw)
    ' - hours from now
    ' Conditions are:
    ' - Latest N Hours
    '
    ' @param Univ Specifies reference to targetuniverse
    Function Universe_AddAdditionalObjectsAndConditions(ByRef Univ As Designer.Universe, ByRef logs As logMessages, ByRef mts As MeasurementTypes) As Boolean
        Dim Cls As Designer.Class
        Dim Obj As Designer.Object
        Dim Cond As Designer.PredefinedCondition
        Dim bo_objects As New BOObjects
        Dim bo_conditions As New BOConditions

        bo_objects.ObjectParse = additionalObjectParse
        bo_conditions.ConditionParse = additionalConditionParse

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" AndAlso mt.RankTable = False Then
                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False AndAlso mt.PlainTable = False Then
                    Cls = Univ.Classes.FindClass(mt.MeasurementTypeID)
                    Obj = bo_objects.addObject(Cls, "data_coverage", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "data_coverage", logs)

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.DATACOVERAGE),sum(DC." & mt.MeasurementTypeID & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_RAW.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.DATACOVERAGE),sum(DC." & mt.MeasurementTypeID & "_RAW.PERIOD_DURATION))"
                    End If

                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration", logs)

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_RAW.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_RAW.PERIOD_DURATION))"
                    End If

                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.MeasurementTypeID & "_Keys")
                    'NE_offset
                    Obj = bo_objects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC", logs)
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY.DC_TIMEZONE,DC." & mt.MeasurementTypeID & "_DAYBH.DC_TIMEZONE,DC." & mt.MeasurementTypeID & "_RAW.DC_TIMEZONE)"
                    Else
                        Obj.Select = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY.DC_TIMEZONE,DC." & mt.MeasurementTypeID & "_RAW.DC_TIMEZONE)"
                    End If

                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    Obj = bo_objects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)", logs)
                    Obj.Select = "DC." & mt.MeasurementTypeID & "_RAW.DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    Obj = bo_objects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now", logs)
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.MeasurementTypeID & "_RAW.DATETIME_ID,now()) as real)/60,0)"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_objects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)", logs)
                    Obj.Select = "DC." & mt.MeasurementTypeID & "_RAW.UTC_DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                    If bo_conditions.ParseCondition(Cond, Cls, logs) = False Then
                        Return False
                    End If


                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                    If bo_conditions.ParseCondition(Cond, Cls, logs) = False Then
                        Return False
                    End If



                End If
                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                    Cls = Univ.Classes.FindClass(mt.MeasurementTypeID)
                    Obj = bo_objects.addObject(Cls, "data_coverage", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "data_coverage", logs)

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.DATACOVERAGE),sum(DC." & mt.MeasurementTypeID & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_COUNT.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.DATACOVERAGE),sum(DC." & mt.MeasurementTypeID & "_COUNT.PERIOD_DURATION))"
                    End If

                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration", logs)

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_COUNT.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.MeasurementTypeID & "_DAY.PERIOD_DURATION),sum(DC." & mt.MeasurementTypeID & "_COUNT.PERIOD_DURATION))"
                    End If

                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.MeasurementTypeID & "_RAW")
                    Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration", logs)
                    Obj.Select = "sum(DC." & mt.MeasurementTypeID & "_RAW.PERIOD_DURATION)"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.MeasurementTypeID & "_RAW_Keys")
                    Obj = bo_objects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC", logs)
                    Obj.Select = "DC." & mt.MeasurementTypeID & "_RAW.DC_TIMEZONE"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If

                    Obj = bo_objects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)", logs)
                    Obj.Select = "DC." & mt.MeasurementTypeID & "_RAW.DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"

                    Obj = bo_objects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now", logs)
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.MeasurementTypeID & "_RAW.DATETIME_ID,now()) as real)/60,0)"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If

                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_objects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)", logs)
                    Obj.Select = "DC." & mt.MeasurementTypeID & "_RAW.UTC_DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If

                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                    If bo_conditions.ParseCondition(Cond, Cls, logs) = False Then
                        Return False
                    End If

                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                    If bo_conditions.ParseCondition(Cond, Cls, logs) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.MeasurementTypeID & "_Keys")
                    'NE_offset
                    Obj = bo_objects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC", logs)
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY.DC_TIMEZONE,DC." & mt.MeasurementTypeID & "_DAYBH.DC_TIMEZONE,DC." & mt.MeasurementTypeID & "_COUNT.DC_TIMEZONE)"
                    Else
                        Obj.Select = "@aggregate_aware(DC." & mt.MeasurementTypeID & "_DAY.DC_TIMEZONE,DC." & mt.MeasurementTypeID & "_COUNT.DC_TIMEZONE)"
                    End If
                    Obj = bo_objects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)", logs)
                    Obj.Select = "DC." & mt.MeasurementTypeID & "_COUNT.DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"

                    Obj = bo_objects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now", logs)
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.MeasurementTypeID & "_COUNT.DATETIME_ID,now()) as real)/60,0)"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If

                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_objects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)", logs)
                    Obj.Select = "DC." & mt.MeasurementTypeID & "_COUNT.UTC_DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                    If bo_conditions.ParseCondition(Cond, Cls, logs) = False Then
                        Return False
                    End If

                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctions.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                    If bo_conditions.ParseCondition(Cond, Cls, logs) = False Then
                        Return False
                    End If

                End If
                If FullAware = False Then
                    If mt.ObjectBusyHours <> "" Then
                        Cls = Univ.Classes.FindClass(mt.MeasurementTypeID & "_BH")
                        Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration", logs)
                        Obj.Select = "sum(DC." & mt.MeasurementTypeID & "_DAYBH.PERIOD_DURATION)"
                        If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                            Return False
                        End If
                    End If
                End If
                If mt.PlainTable = True Then
                    Cls = Univ.Classes.FindClass(mt.MeasurementTypeID)
                    Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration", logs)
                    Obj.Select = "sum(DC." & mt.MeasurementTypeID & ".PERIOD_DURATION)"
                    If bo_objects.ParseObject(Obj, Cls, logs) = False Then
                        Return False
                    End If
                End If
            End If
        Next mt_count
        Return True
    End Function


    ''
    ' Updates universe.
    '
    ' @param Filename Specifies TP definition's filename.
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    ' @param BaseFilename Specifies base definition's filename.
    Function UpdateUniverse(ByRef Filename As String, ByRef CMTechPack As Boolean, ByRef BaseFilename As String, ByRef OutputDir_Original As String, ByRef BOVersion As String) As logMessages

        Dim DesignerApp As Designer.Application

        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim Result As MsgBoxResult
        Dim checkItems As Designer.CheckedItems
        Dim checkItem As Designer.CheckedItem
        Dim logs As logMessages
        Dim OutputDir As String
        Dim count As Integer

        logs = New logMessages

        FullAware = True

        'zero update information
        updatedTables = ""
        updatedClasses = ""
        updatedObjects = ""
        updatedConditions = ""
        updatedJoins = ""
        updatedContexts = ""
        extra_joins = New UnivJoins

        'update build number
        Dim tp_excel = New TPExcelWriter
        Dim updateBuild = tp_excel.updateBuildNumber(Filename, "universe", OutputDir_Original)
        tp_excel = Nothing

        tpAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Filename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""
        baseAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & BaseFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""

        tpConn = New System.Data.OleDb.OleDbConnection(tpAdoConn)
        baseConn = New System.Data.OleDb.OleDbConnection(baseAdoConn)

        tpConn.Open()
        baseConn.Open()

        DesignerApp = New Designer.Application
        DesignerApp.Visible = False
        If BOVersion = "6.5" Then
            DesignerApp.LoginAs()
        ElseIf BOVersion = "XI" Then
            DesignerApp.LogonDialog()
        Else
            MsgBox("Invalid BO Version. Contact support.")
        End If


        ClsInit = 0
        ClsInit = Initialize_Classes(logs)
        UpdateVersionProperties(OutputDir_Original)
        If ClsInit = 1 Then
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
        Else
            For count = 1 To UnvMts.Count
                UnvMt = UnvMts.Item(count)
                logs.mergeLogs(upgradeUniverse(DesignerApp, UnvMt.MeasurementTypes, _
                UnvMt.ReferenceTypes, UnvMt.VectorReferenceTypes, _
                UnvMt.UnivJoins, UnvMt.ReferenceDatas, UnvMt.VectorReferenceDatas, _
                OutputDir_Original, BOVersion, UnvMt.UniverseNameExtension, UnvMt.UniverseExtension, CMTechPack))
            Next count

        End If

        tpConn.Close()
        baseConn.Close()
        DesignerApp.Quit()

        Return logs

    End Function

    Function upgradeUniverse(ByVal DesignerApp As Designer.Application, ByRef mts As MeasurementTypes, ByRef rts As ReferenceTypes, _
    ByRef vector_rts As ReferenceTypes, ByRef univ_joins As UnivJoins, ByRef rds As ReferenceDatas, ByRef vector_rds As ReferenceDatas, _
    ByRef OutputDir_Original As String, ByRef BOVersion As String, ByRef UniverseNameExtension As String, ByRef UniverseExtension As String, ByRef CMTechPack As Boolean) As logMessages

        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim logs As logMessages
        Dim OutputDir As String

        logs = New logMessages

        'zero update information
        updatedTables = ""
        updatedClasses = ""
        updatedObjects = ""
        updatedConditions = ""
        updatedJoins = ""
        updatedContexts = ""
        extra_joins = New UnivJoins

        FullAware = True

        Dim Message As String
        If UniverseExtension <> "" Then
            Message = "Open Universe '" & UniverseName & " " & UniverseExtension & "' with filename '"
        Else
            Message = "Open Universe '" & UniverseName & "' with filename '"
        End If
        If BOVersion = "6.5" Then
            If UniverseNameExtension <> "" Then
                Message &= UniverseFileName & UniverseNameExtension & "'."
            Else
                Message &= UniverseFileName & "'."
            End If
        ElseIf BOVersion = "XI" Then
            If UniverseExtension <> "" Then
                Message &= UniverseName & " " & UniverseExtension & "'."
            Else
                Message &= UniverseName & "'."
            End If
        Else
            MsgBox("Invalid BO Version. Contact support.")
        End If

        Dim SaveFileName As String
        If BOVersion = "6.5" Then
            If UniverseNameExtension <> "" Then
                SaveFileName = UniverseFileName & UniverseNameExtension
            Else
                SaveFileName = UniverseFileName
            End If
        ElseIf BOVersion = "XI" Then
            If UniverseExtension <> "" Then
                SaveFileName = UniverseName & " " & UniverseExtension
            Else
                SaveFileName = UniverseName
            End If
        Else
            MsgBox("Invalid BO Version. Contact support.")
        End If

        MsgBox(Message)
        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Open
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Visible = False
                DesignerApp.Interactive = False
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        System.Threading.Thread.Sleep(2000) ' Sleep for 2 seconds

        ObjectBHSupport = getObjectBHSupport(mts)
        ElementBHSupport = getElementBHSupport(mts)

        If UniverseExtension <> "" Then
            SetParameter(Univ, "Name", UniverseName & " " & UniverseExtension)
            logs.AddLogText("Log on Upgrade Universe: " & UniverseName & " " & UniverseExtension)
        Else
            SetParameter(Univ, "Name", UniverseName)
            logs.AddLogText("Log on Upgrade Universe: " & UniverseName & " " & UniverseExtension)
        End If
        SetParameter(Univ, "Description", UniverseDescription)

        Try
            SetParameter(Univ, "Connection", DesignerApp.Connections(1).Name)
        Catch ex As Exception
            logs.AddLogText("Universe Connection Error: " & ex.Message & ".")
        End Try

        If Universe_AddTables(Univ, ObjectBHSupport, ElementBHSupport, False, logs, mts, rts, vector_rts, UniverseNameExtension) = False Then
            logs.AddLogText("Universe upgrade stopped while adding tables.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Exit Function
        End If

        Universe_RemoveContexts(Univ, logs)

        If Universe_AddJoins(Univ, logs, mts, univ_joins) = False Then
            logs.AddLogText("Universe upgrade stopped while adding joins.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Exit Function
        End If


        'Univ.RefreshStructure()
        'Univ.ArrangeTables()

        If Universe_AddClasses(Univ, ObjectBHSupport, ElementBHSupport, False, logs, UniverseNameExtension) = False Then
            logs.AddLogText("Universe upgrade stopped while adding classes.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If


        If Universe_AddCounters(Univ, CMTechPack, logs, mts, vector_rds) = False Then
            logs.AddLogText("Universe upgrade stopped while adding counters.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddCounterKeys(Univ, logs, mts) = False Then
            logs.AddLogText("Universe upgrade stopped while adding counter keys.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddAdditionalObjectsAndConditions(Univ, logs, mts) = False Then
            logs.AddLogText("Universe upgrade stopped while adding additional objects and conditions. Please, Contact Support.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddObjects(Univ, ObjectBHSupport, ElementBHSupport, logs, mts, rds, UniverseNameExtension) = False Then
            logs.AddLogText("Universe upgrade stopped while adding objects.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddConditions(Univ, ObjectBHSupport, ElementBHSupport, logs, mts, rds, UniverseNameExtension) = False Then
            logs.AddLogText("Universe upgrade stopped while adding conditions.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        'obsolete check not done for this
        Universe_AddIncompatibleObjects(Univ, logs, mts)

        'check obsolete content
        Dim Tbl As Designer.Table
        Dim Jn As Designer.Join
        Dim Cntxt As Designer.Context
        Dim Cls As Designer.Class
        Dim count As Integer
        Dim found As Boolean

        logs.AddLogText(" ")
        logs.AddLogText("Obsolete information: " & Univ.LongName & " (" & SaveFileName & ")")
        logs.AddLogText("Obsolete universe tables:")
        For count = 1 To Univ.Tables.Count
            Tbl = Univ.Tables.Item(count)
            If InStrRev(updatedTables, Tbl.Name & ";") = 0 Then
                logs.AddLogText(Tbl.Name)
            End If
        Next count

        logs.AddLogText(" ")
        logs.AddLogText("Obsolete universe joins:")
        For Each Jn In Univ.Joins
            If InStrRev(updatedJoins, Jn.Expression & ";") = 0 Then
                logs.AddLogText(Jn.Expression)
            End If
        Next Jn

        logs.AddLogText(" ")
        logs.AddLogText("Obsolete universe contexts:")
        For Each Cntxt In Univ.Contexts
            If InStrRev(updatedContexts, Cntxt.Name & ";") = 0 Then
                logs.AddLogText(Cntxt.Name)
            End If
        Next Cntxt

        logs.AddLogText(" ")
        logs.AddLogText("Obsolete universe classes:")
        For Each Cls In Univ.Classes
            checkClass(Cls, updatedClasses, logs)
        Next Cls

        logs.AddLogText(" ")
        logs.AddLogText("Obsolete universe objects:")
        For Each Cls In Univ.Classes
            checkObject(Cls, updatedObjects, logs)
        Next Cls

        logs.AddLogText(" ")
        logs.AddLogText("Obsolete universe conditions:")
        For Each Cls In Univ.Classes
            checkCondition(Cls, updatedConditions, logs)
        Next Cls



        If SaveFileName <> Univ.Name Then
            retry = True
            While retry = True
                Try
                    retry = False
                    If OutputDir_Original <> "" Then
                        OutputDir = OutputDir_Original & "\unv"
                        Try
                            If Not System.IO.Directory.Exists(OutputDir) Then
                                System.IO.Directory.CreateDirectory(OutputDir)
                            End If
                        Catch ex As Exception
                            MsgBox("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
                        End Try
                        Univ.SaveAs(OutputDir & "\" & SaveFileName)
                    Else
                        Univ.SaveAs(SaveFileName)
                    End If
                Catch ex As Exception
                    If OutputDir <> "" Then
                        MsgBox("Saving failed to : " & OutputDir & "\" & SaveFileName & ".")
                    Else
                        MsgBox("Saving failed to : " & SaveFileName & ".")
                    End If

                    System.Threading.Thread.Sleep(2000)
                    'retry = True
                End Try
            End While
        Else
            retry = True
            While retry = True
                Try
                    retry = False
                    Univ.Save()
                Catch ex As Exception
                    MsgBox("Saving failed to : " & Univ.Name & ".")
                    System.Threading.Thread.Sleep(2000)
                    'retry = True
                End Try
            End While
        End If

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Close()
            Catch ex As Exception
                MsgBox("Closing failed of : " & Univ.Name & ".")
            End Try
        End While

        Return logs

    End Function

    Private Function checkClass(ByRef Cls As Designer.Class, ByRef checkString As String, ByRef logs As logMessages) As Boolean
        Dim SubCls As Designer.Class
        If InStrRev(checkString, Cls.Name & ";") = 0 Then
            logs.AddLogText(Cls.Name)
        End If
        For Each SubCls In Cls.Classes
            checkClass(SubCls, checkString, logs)
        Next
    End Function
    Private Function checkObject(ByRef Cls As Designer.Class, ByRef checkString As String, ByRef logs As logMessages) As Boolean
        Dim SubCls As Designer.Class
        Dim Obj As Designer.Object
        For Each Obj In Cls.Objects
            If InStrRev(checkString, Cls.Name & "/" & Obj.Name & ";") = 0 Then
                logs.AddLogText(Cls.Name & "/" & Obj.Name)
            End If
        Next
        For Each SubCls In Cls.Classes
            checkObject(SubCls, checkString, logs)
        Next
    End Function
    Private Function checkCondition(ByRef Cls As Designer.Class, ByRef checkString As String, ByRef logs As logMessages) As Boolean
        Dim SubCls As Designer.Class
        Dim Cond As Designer.PredefinedCondition
        For Each Cond In Cls.PredefinedConditions
            If InStrRev(checkString, Cls.Name & "/" & Cond.Name & ";") = 0 Then
                logs.AddLogText(Cls.Name & "/" & Cond.Name)
            End If
        Next
        For Each SubCls In Cls.Classes
            checkCondition(SubCls, checkString, logs)
        Next
    End Function

    Function MakeVerificationReports(ByRef BoApp As busobj.Application, ByRef Filename As String, ByRef BaseFilename As String, ByRef CMData As Boolean, ByRef OutputDir_Original As String, ByRef BOVersion As String) As logMessages

        Dim Found As Boolean
        Dim ReportList As String
        Dim ResultCount As MsgBoxResult
        Dim ResultDay As MsgBoxResult
        Dim ResultDayBH As MsgBoxResult
        Dim ResultElemBH As MsgBoxResult
        Dim ClsInit As Integer
        Dim logs As logMessages
        Dim OutputDir As String
        Dim count As Integer

        logs = New logMessages

        FullAware = True

        tpAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Filename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""
        baseAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & BaseFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""

        tpConn = New System.Data.OleDb.OleDbConnection(tpAdoConn)
        baseConn = New System.Data.OleDb.OleDbConnection(baseAdoConn)

        tpConn.Open()
        baseConn.Open()

        'Open BO
        If BOVersion = "6.5" Then
            BoApp.LoginAs()
        ElseIf BOVersion = "XI" Then
            BoApp.LogonDialog()
        Else
            MsgBox("Invalid BO Version. Contact support.")
        End If
        BoApp.Interactive = False

        ClsInit = 0

        ClsInit = Initialize_Classes(logs)
        If ClsInit = 1 Then
            tpConn.Close()
            baseConn.Close()
            BoApp.Quit()
        Else
            For count = 1 To UnvMts.Count
                UnvMt = UnvMts.Item(count)
                logs.mergeLogs(CreateVerificationReports(BoApp, CMData, OutputDir_Original, BOVersion, UnvMt.MeasurementTypes, UnvMt.UniverseExtension))
            Next count
        End If

        tpConn.Close()
        baseConn.Close()
        BoApp.Interactive = True

        Return logs

    End Function

    Function CreateVerificationReports(ByRef BoApp As busobj.Application, ByVal CMData As Boolean, ByRef OutputDir_Original As String, ByRef BOVersion As String, ByRef mts As MeasurementTypes, ByRef UniverseExtension As String) As logMessages

        Dim Found As Boolean
        Dim ReportList As String
        Dim ResultCount As MsgBoxResult
        Dim ResultDay As MsgBoxResult
        Dim ResultDayBH As MsgBoxResult
        Dim ResultElemBH As MsgBoxResult
        Dim ClsInit As Integer
        Dim logs As logMessages
        Dim OutputDir As String

        logs = New logMessages

        FullAware = True

        OutputDir = OutputDir_Original & "\rep"
        Try
            If Not System.IO.Directory.Exists(OutputDir) Then
                System.IO.Directory.CreateDirectory(OutputDir)
            End If
        Catch ex As Exception
            MsgBox("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
            Return logs
        End Try

        If CMData = True Then
            Call VerifReports_makeVerificationReport_CM(BoApp, OutputDir, logs, mts, UniverseExtension)
        Else
            Found = False
            ReportList = ""
            For mt_count = 1 To mts.Count
                mt = mts.Item(mt_count)
                If mt.MeasurementTypeID <> "" Then
                    If mt.CreateCountTable = True Then
                        Found = True
                        ReportList &= mt.MeasurementTypeID & ", "
                    End If
                End If
            Next mt_count
            If Found = True Then
                ResultCount = MsgBox("Following COUNT Total Verification Reports should be created." & Chr(10) & ReportList & Chr(10) & _
                "Do you want to create them all? " & Chr(10) & _
                "Select Yes to create all." & Chr(10) & _
                "Select No to verify creation per measurement." & Chr(10) & _
                "Select Cancel to skip report creation.", MsgBoxStyle.YesNoCancel)
            End If

            Found = False
            ReportList = ""
            For mt_count = 1 To mts.Count
                mt = mts.Item(mt_count)
                If mt.MeasurementTypeID <> "" Then
                    If mt.RankTable = False Then
                        Found = True
                        ReportList &= mt.MeasurementTypeID & ", "
                    End If
                End If
            Next mt_count
            If Found = True Then
                ResultDay = MsgBox("Following DAY Total Verification Reports should be created." & Chr(10) & ReportList & Chr(10) & _
                "Do you want to create them all? " & Chr(10) & _
                "Select Yes to create all." & Chr(10) & _
                "Select No to verify creation per measurement." & Chr(10) & _
                "Select Cancel to skip report creation.", MsgBoxStyle.YesNoCancel)
            End If

            Found = False
            ReportList = ""
            For mt_count = 1 To mts.Count
                mt = mts.Item(mt_count)
                If mt.MeasurementTypeID <> "" Then
                    If mt.RankTable = False And mt.ObjectBusyHours <> "" Then
                        Found = True
                        ReportList &= mt.MeasurementTypeID & ", "
                    End If
                End If
            Next mt_count
            If Found = True Then
                ResultDayBH = MsgBox("Following DAYBH Busy Hour Verification Reports should be created." & Chr(10) & ReportList & Chr(10) & _
                "Do you want to create them all? " & Chr(10) & _
                "Select Yes to create all." & Chr(10) & _
                "Select No to verify creation per measurement." & Chr(10) & _
                "Select Cancel to skip report creation.", MsgBoxStyle.YesNoCancel)
            End If

            Found = False
            ReportList = ""
            For mt_count = 1 To mts.Count
                mt = mts.Item(mt_count)
                If mt.MeasurementTypeID <> "" Then
                    If mt.RankTable = False And mt.ElementBusyHours = True Then
                        Found = True
                        ReportList &= mt.MeasurementTypeID & ", "
                    End If
                End If
            Next mt_count
            If Found = True Then
                ResultElemBH = MsgBox("Following ELEMBH Busy Hour Verification Reports should be created." & Chr(10) & ReportList & Chr(10) & _
                "Do you want to create them all? " & Chr(10) & _
                "Select Yes to create all." & Chr(10) & _
                "Select No to verify creation per measurement." & Chr(10) & _
                "Select Cancel to skip report creation.", MsgBoxStyle.YesNoCancel)
            End If

            'Create Count verification reports
            If ResultCount = MsgBoxResult.Yes Then
                VerifReports_makeVerificationReport_Count(BoApp, OutputDir, logs, False, mts, UniverseExtension)
            End If
            If ResultCount = MsgBoxResult.No Then
                VerifReports_makeVerificationReport_Count(BoApp, OutputDir, logs, True, mts, UniverseExtension)
            End If
            'Create Day verification reports
            If ResultDay = MsgBoxResult.Yes Then
                VerifReports_makeVerificationReport_Day(BoApp, OutputDir, logs, False, mts, UniverseExtension)
            End If
            If ResultDay = MsgBoxResult.No Then
                VerifReports_makeVerificationReport_Day(BoApp, OutputDir, logs, True, mts, UniverseExtension)
            End If
            'Create DAYBH Busy Hour verification reports
            If ResultDayBH = MsgBoxResult.Yes Then
                VerifReports_makeVerificationReport_DayBH(BoApp, OutputDir, logs, False, mts, UniverseExtension)
            End If

            If ResultDayBH = MsgBoxResult.No Then
                VerifReports_makeVerificationReport_DayBH(BoApp, OutputDir, logs, True, mts, UniverseExtension)
            End If

            'Create ELEMBH Busy Hour verification reports
            If ResultElemBH = MsgBoxResult.Yes Then
                VerifReports_makeVerificationReport_ElemBH(BoApp, OutputDir, logs, False, mts, UniverseExtension)
            End If
            If ResultElemBH = MsgBoxResult.No Then
                VerifReports_makeVerificationReport_ElemBH(BoApp, OutputDir, logs, True, mts, UniverseExtension)
            End If

        End If

        Return logs

    End Function

    Function UpdateVersionProperties(ByRef OutputDir_Original As String) As logMessages


        Dim metafile As String
        Dim iFileNum As Short
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer
        Dim OutputDir As String
        Dim readString As String

        logs = New logMessages


        OutputDir = OutputDir_Original & "\install"
        Try
            If Not System.IO.Directory.Exists(OutputDir) Then
                System.IO.Directory.CreateDirectory(OutputDir)
            End If
        Catch ex As Exception
            MsgBox("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
        End Try

        metafile = OutputDir & "\version.properties"

        'read file information
        Dim buildTag As String
        Dim buildNumber As String
        Dim requiredTechPack As String
        Dim requiredList() As String
        Try
            iFileNum = FreeFile()
            FileOpen(iFileNum, metafile, OpenMode.Input)
            Do Until EOF(iFileNum)
                readString = LineInput(iFileNum)
                If InStrRev(readString, "build.tag=") > 0 Then
                    buildTag = readString
                End If
                If InStrRev(readString, "build.buildnumber=") > 0 Then
                    buildNumber = readString
                End If
                If InStrRev(readString, "required_tech_packs.") > 0 Then
                    requiredTechPack &= Replace(readString, "required_tech_packs.", "") & ","
                End If
            Loop
            FileClose(iFileNum)
        Catch ex As Exception
            'do nothing
        End Try

        Try
            If System.IO.File.Exists(metafile) Then
                Kill(metafile)
            End If
        Catch ex As Exception
            'do nothing
        End Try


        iFileNum = FreeFile()
        FileOpen(iFileNum, metafile, OpenMode.Output)
        PrintLine(iFileNum, "#", Now())
        PrintLine(iFileNum, "tech_pack.name=" & TechPackName)
        PrintLine(iFileNum, "tech_pack.version=" & TPReleaseVersion)
        If buildTag Is Nothing Then
            PrintLine(iFileNum, "build.tag=")
        Else
            PrintLine(iFileNum, buildTag)
        End If
        If buildNumber Is Nothing Then
            PrintLine(iFileNum, "build.buildnumber=")
        Else
            PrintLine(iFileNum, buildNumber)
        End If
        PrintLine(iFileNum, "universe_build.buildnumber=" & TPVersion)
        If Not requiredTechPack Is Nothing Then
            requiredList = Split(requiredTechPack)
            For count = 0 To UBound(requiredList) - 1
                PrintLine(iFileNum, "required_tech_packs." & requiredList(count))
            Next count
        End If

        FileClose(iFileNum)

        Return logs


    End Function

    ''
    ' Makes universe.
    '
    ' @param Filename Specifies TP definition's filename.
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    ' @param BaseFilename Specifies base definition's filename.
    Function MakeUniverse(ByRef Filename As String, ByRef CMTechPack As Boolean, ByRef BaseFilename As String, ByRef OutputDir_Original As String, ByRef BOVersion As String) As logMessages

        Dim DesignerApp As Designer.Application

        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim Result As MsgBoxResult
        Dim checkItems As Designer.CheckedItems
        Dim checkItem As Designer.CheckedItem
        Dim logs As logMessages
        Dim OutputDir As String
        Dim count As Integer

        logs = New logMessages

        extra_joins = New UnivJoins

        FullAware = True

        'update build number
        Dim tp_excel = New TPExcelWriter
        Dim updateBuild = tp_excel.updateBuildNumber(Filename, "universe", OutputDir_Original)
        tp_excel = Nothing

        tpAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Filename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""
        baseAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & BaseFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""

        tpConn = New System.Data.OleDb.OleDbConnection(tpAdoConn)
        baseConn = New System.Data.OleDb.OleDbConnection(baseAdoConn)

        tpConn.Open()
        baseConn.Open()

        DesignerApp = New Designer.Application
        DesignerApp.Visible = False
        If BOVersion = "6.5" Then
            DesignerApp.LoginAs()
        ElseIf BOVersion = "XI" Then
            DesignerApp.LogonDialog()
        Else
            MsgBox("Invalid BO Version. Contact support.")
        End If


        ClsInit = 0

        ClsInit = Initialize_Classes(logs)
        UpdateVersionProperties(OutputDir_Original)
        If ClsInit = 1 Then
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
        Else
            For count = 1 To UnvMts.Count
                UnvMt = UnvMts.Item(count)
                logs.mergeLogs(createUniverse(DesignerApp, UnvMt.MeasurementTypes, _
                UnvMt.ReferenceTypes, UnvMt.VectorReferenceTypes, _
                UnvMt.UnivJoins, UnvMt.ReferenceDatas, UnvMt.VectorReferenceDatas, _
                OutputDir_Original, BOVersion, UnvMt.UniverseNameExtension, UnvMt.UniverseExtension, CMTechPack, count))
            Next count
        End If

        tpConn.Close()
        baseConn.Close()
        DesignerApp.Quit()

        Return logs

    End Function
    Function createUniverse(ByVal DesignerApp As Designer.Application, ByRef mts As MeasurementTypes, ByRef rts As ReferenceTypes, _
    ByRef vector_rts As ReferenceTypes, ByRef univ_joins As UnivJoins, ByRef rds As ReferenceDatas, ByRef vector_rds As ReferenceDatas, _
    ByRef OutputDir_Original As String, ByRef BOVersion As String, ByRef UniverseNameExtension As String, ByRef UniverseExtension As String, ByRef CMTechPack As Boolean, ByRef UniverseCount As Integer) As logMessages

        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim logs As logMessages
        Dim OutputDir As String

        logs = New logMessages

        extra_joins = New UnivJoins

        FullAware = True

        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Add
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Visible = False
                DesignerApp.Interactive = False
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        System.Threading.Thread.Sleep(2000) ' Sleep for 2 seconds

        ObjectBHSupport = getObjectBHSupport(mts)
        ElementBHSupport = getElementBHSupport(mts)

        If UniverseExtension <> "" Then
            SetParameter(Univ, "Name", UniverseName & " " & UniverseExtension)
            logs.AddLogText("Log on Create Universe: " & UniverseName & " " & UniverseExtension)
        Else
            SetParameter(Univ, "Name", UniverseName)
            logs.AddLogText("Log on Create Universe: " & UniverseName)
        End If

        SetParameter(Univ, "Description", UniverseDescription)
        Try
            SetParameter(Univ, "Connection", DesignerApp.Connections(1).Name)
        Catch ex As Exception
            logs.AddLogText("Universe Connection Error: " & ex.Message & ".")
        End Try


        If Universe_AddTables(Univ, ObjectBHSupport, ElementBHSupport, True, logs, mts, rts, vector_rts, UniverseNameExtension) = False Then
            logs.AddLogText("Universe creation stopped while adding tables.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Exit Function
        End If

        If Universe_AddJoins(Univ, logs, mts, univ_joins) = False Then
            logs.AddLogText("Universe creation stopped while adding joins.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Exit Function
        End If

        'Univ.RefreshStructure()
        'Univ.ArrangeTables()

        Try
            Univ.ControlOption.LimitSizeofResultSet = True
            Univ.ControlOption.LimitExecutionTime = True
            Univ.ControlOption.LimitSizeOfLongTextObject = False
            Univ.ControlOption.WarnIfCostEstimateExceeded = False

            Univ.ControlOption.LimitSizeofResultSetValue = 250000
            Univ.ControlOption.LimitExecutionTimeValue = 120
        Catch ex As Exception
            logs.AddLogText("Universe Control Option Exception: " & ex.ToString)
        End Try

        If Universe_AddClasses(Univ, ObjectBHSupport, ElementBHSupport, True, logs, UniverseNameExtension) = False Then
            logs.AddLogText("Universe creation stopped while adding classes.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddCounters(Univ, CMTechPack, logs, mts, vector_rds) = False Then
            logs.AddLogText("Universe creation stopped while adding counters.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddCounterKeys(Univ, logs, mts) = False Then
            logs.AddLogText("Universe creation stopped while adding counter keys.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddAdditionalObjectsAndConditions(Univ, logs, mts) = False Then
            logs.AddLogText("Universe creation stopped while adding additional objects and conditions. Please, Contact Support.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        If Universe_AddObjects(Univ, ObjectBHSupport, ElementBHSupport, logs, mts, rds, UniverseNameExtension) = False Then
            logs.AddLogText("Universe creation stopped while adding objects.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If


        If Universe_AddConditions(Univ, ObjectBHSupport, ElementBHSupport, logs, mts, rds, UniverseNameExtension) = False Then
            logs.AddLogText("Universe creation stopped while adding conditions.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return logs
        End If

        Universe_AddIncompatibleObjects(Univ, logs, mts)

        Dim SaveFileName As String
        If BOVersion = "6.5" Then
            If UniverseNameExtension <> "" Then
                SaveFileName = UniverseFileName & UniverseNameExtension
            Else
                SaveFileName = UniverseFileName
            End If
        ElseIf BOVersion = "XI" Then
            If UniverseExtension <> "" Then
                SaveFileName = UniverseName & " " & UniverseExtension
            Else
                SaveFileName = UniverseName
            End If
        Else
            MsgBox("Invalid BO Version. Contact support.")
        End If

        retry = True
        While retry = True
            Try
                retry = False
                If OutputDir_Original <> "" Then
                    OutputDir = OutputDir_Original & "\unv"
                    Try
                        If Not System.IO.Directory.Exists(OutputDir) Then
                            System.IO.Directory.CreateDirectory(OutputDir)
                        End If
                    Catch ex As Exception
                        MsgBox("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
                    End Try
                    Univ.SaveAs(OutputDir & "\" & SaveFileName)
                Else
                    Univ.SaveAs(SaveFileName)
                End If
            Catch ex As Exception
                If OutputDir <> "" Then
                    MsgBox("Saving failed to : " & OutputDir & "\" & SaveFileName & ".")
                Else
                    MsgBox("Saving failed to : " & SaveFileName & ".")
                End If

                System.Threading.Thread.Sleep(2000)
                'retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Close()
            Catch ex As Exception
                MsgBox("Closing failed of : " & SaveFileName & ".")
            End Try
        End While

        Return logs

    End Function
    Sub SetParameter(ByRef Univ As Object, ByRef Parameter As String, ByRef Value As String)
        Dim retry As Boolean
        retry = True
        While retry = True
            Try
                retry = False
                If Parameter = "Name" Then
                    Univ.LongName = Value
                End If
                If Parameter = "Description" Then
                    Univ.Description = Value
                End If
                If Parameter = "Connection" Then
                    Univ.Connection = Value
                End If
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While
    End Sub
    ''
    ' Initializes classes. Reads TP definitions. 
    '
    ' @return 0, if initialization is successful
    Public Function Initialize_Classes(ByRef logs As logMessages) As Integer

        Dim Description As String
        Dim tp_utils = New TPUtilities
        Dim Success As Boolean


        TechPackName = tp_utils.readSingleValue("Coversheet$B2:B3", tpConn, dbCommand, dbReader)
        TechPackType = tp_utils.readSingleValue("Coversheet$B3:B4", tpConn, dbCommand, dbReader)
        UniverseFileName = tp_utils.readSingleValue("Coversheet$B4:B5", tpConn, dbCommand, dbReader)
        Description = tp_utils.readSingleValue("Coversheet$B5:B6", tpConn, dbCommand, dbReader)
        TPReleaseVersion = tp_utils.readSingleValue("Coversheet$B6:B7", tpConn, dbCommand, dbReader)
        TPVersion = tp_utils.readSingleValue("Coversheet$C6:C7", tpConn, dbCommand, dbReader)
        VendorRelease = tp_utils.readSingleValue("Coversheet$B7:B8", tpConn, dbCommand, dbReader)
        ProductNumber = tp_utils.readSingleValue("Coversheet$B8:B9", tpConn, dbCommand, dbReader)

        DefaultKeyMaxAmount = tp_utils.readSingleValue("Coversheet$B7:B8", baseConn, dbCommand, dbReader)
        DefaultCounterMaxAmount = tp_utils.readSingleValue("Coversheet$B8:B9", baseConn, dbCommand, dbReader)

        Dim ExtendedUnvList() As String
        ExtendedUnvList = tp_utils.readSingleValue("Coversheet$C4:C5", tpConn, dbCommand, dbReader).Split(",")

        UniverseName = "TP " & Description & " " & TechPackType
        'UniverseDescription = UniverseName & " " & TPVersion
        'UniverseDescription = UniverseName & ":b" & TPVersion & Chr(10) & "Release: " & TPReleaseVersion

        UniverseDescription = Description & " " & TechPackType & " " & Chr(10) & "Version: b" & TPVersion & " " & Chr(10) & _
        "Vendor releases: " & VendorRelease & " " & Chr(10) & _
        "Product: " & ProductNumber & " " & TPReleaseVersion


        If UniverseName.Length > 35 Then
            logs.AddLogText("Universe name '" & UniverseName & "' exceeds maximum of 35 characters.")
        End If

        all_cnts = New Counters
        all_cnts.getCounters(DefaultCounterMaxAmount, tpConn, dbCommand, dbReader, logs)

        all_cnt_keys = New CounterKeys
        all_cnt_keys.getCounterKeys(DefaultKeyMaxAmount, tpConn, dbCommand, dbReader, logs)

        pub_keys = New PublicKeys
        pub_keys.getPublicKeys(baseConn, dbCommand, dbReader, logs)


        UnvMts = New UniverseMeasurements

        original_mts = New MeasurementTypes
        original_mts.getMeasurements(TechPackName, tpConn, dbCommand, dbReader, all_cnts, all_cnt_keys, pub_keys, logs)




        Dim count As Integer
        Dim UnvCount As Integer
        Dim UniverseNamextension As String
        Dim UniverseExtension As String
        Dim UniverseCountList() As String

        For count = 0 To UBound(ExtendedUnvList)
            Dim UniverseInfo() As String
            If ExtendedUnvList(count) = "" Then
                UniverseNamextension = ""
                UniverseExtension = ""
            Else
                UniverseInfo = ExtendedUnvList(count).Split("=")
                UniverseNamextension = LCase(UniverseInfo(0))
                UniverseExtension = UniverseInfo(1)
            End If

            Dim mts = New MeasurementTypes
            Dim univ_joins = New UnivJoins
            Dim rts = New ReferenceTypes
            Dim rds = New ReferenceDatas
            Dim vector_rts = New ReferenceTypes
            Dim vector_rds = New ReferenceDatas
            For mt_count = 1 To original_mts.Count
                mt = original_mts.Item(mt_count)
                'handle ALL
                If mt.ExtendedUniverse = "all" Then
                    mts.AddItem(mt)
                ElseIf mt.ExtendedUniverse = "" AndAlso UniverseNamextension = "" Then
                    mts.AddItem(mt)
                Else
                    If InStrRev(mt.ExtendedUniverse, ",") = 0 Then
                        If mt.ExtendedUniverse = UniverseNamextension Then
                            mts.AddItem(mt)
                        End If
                    Else
                        UniverseCountList = Split(mt.ExtendedUniverse, ",")
                        For UnvCount = 0 To UBound(UniverseCountList)
                            If UniverseCountList(UnvCount) = UniverseNamextension Then
                                mts.AddItem(mt)
                            End If
                        Next
                    End If
                End If
            Next mt_count
            univ_joins.buildLists(mts)
            If univ_joins.getJoins(TechPackName, mts, baseConn, dbCommand, dbReader, logs) = False Then
                Return 1
            End If
            If univ_joins.getJoins(TechPackName, mts, tpConn, dbCommand, dbReader, logs) = False Then
                Return 1
            End If
            univ_joins.getVectorJoins(mts)

            rts.getTopology(TechPackName, tpConn, dbCommand, dbReader, mts, logs)
            rts.getTopology(TechPackName, baseConn, dbCommand, dbReader, mts, logs)

            rds.getTopology(TechPackName, tpConn, dbCommand, dbReader, mts, logs, Nothing)
            rds.getTopology(TechPackName, baseConn, dbCommand, dbReader, mts, logs, rts)

            vector_rts.getVectorTopology(mts)
            vector_rds.getVectorTopology(mts, logs)

            UnvMt = New UniverseMeasurements.UniverseMeasurement
            UnvMt.UniverseExtension = UniverseExtension
            UnvMt.UniverseNameExtension = UniverseNamextension
            UnvMt.MeasurementTypes = mts
            UnvMt.ReferenceTypes = rts
            UnvMt.ReferenceDatas = rds
            UnvMt.UnivJoins = univ_joins
            UnvMt.VectorReferenceTypes = vector_rts
            UnvMt.VectorReferenceDatas = vector_rds
            UnvMts.AddItem(UnvMt)
        Next count



        univ_clss = New UnivClasses
        Success = univ_clss.getClasses(baseConn, dbCommand, dbReader, logs)
        If Success = False Then
            Return 1
        End If
        Success = univ_clss.getClasses(tpConn, dbCommand, dbReader, logs)
        If Success = False Then
            Return 1
        End If

        repobjs = New ReportObjects
        repobjs.getReportObjects(tpConn, dbCommand, dbReader, logs)

        repconds = New ReportConditions
        repconds.getReportConditions(tpConn, dbCommand, dbReader, logs)

        Return 0

    End Function

    Sub VerifReports_makeVerificationReport_CM(ByRef BoApp As busobj.Application, ByRef OutputDir As String, ByRef logs As logMessages, ByRef mts As MeasurementTypes, ByRef UniverseExtension As String)

        Dim Doc As busobj.Document
        Dim unvObj As busobj.Universe
        Dim claObj As busobj.Class
        Dim claObj2 As busobj.Class
        Dim claObj3 As busobj.Class
        Dim claObj4 As busobj.Class
        Dim dp As busobj.DataProvider
        Dim dp2 As busobj.DataProvider
        Dim rep As busobj.Report
        Dim RepName As String

        'Make Total Verification reports
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then

                Doc = BoApp.Documents.Add 'Add new document

                dp = loadDataProvider(BoApp, Doc, "CM", logs, UniverseExtension)
                If dp Is Nothing Then
                    Exit Sub
                End If
                unvObj = dp.Universe 'Set universe
                claObj = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Parameters", "", logs, mts)  'Get measurement objects
                claObj2 = VerifReports_ClassObject(claObj, mt.MeasurementTypeID, "", logs, mts)  'Get measurement objects
                VerifReports_AddObjects(dp, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add objects
                VerifReports_AddClassKeyObjects(dp, claObj2, logs) 'Add measurement keys
                VerifReports_AddCMClassObjects(dp, claObj, mt.MeasurementTypeID, mt.Counters, logs) 'Add measurement counters
                VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "TOPOLOGY", logs) 'Add topology conditions
                VerifReports_AddKeyConditions(Doc, dp, mt.MeasurementTypeID, "KEYTOPOLOGY", "", logs) 'Add topology conditions
                VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add time conditions
                unloadDataProvider("CM", dp, logs)

                'Add new report
                RepName = "Verification_" & claObj.Name
                Try
                    rep = Doc.Reports.CreateQuickReport(dp.Name)
                    applyReportSettings(Doc, rep, "Verification_CM_Template.ret", RepName, logs)
                    VerifReports_BuildRawReportTables(Doc, rep, logs)
                    VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "RAW", mt.Counters)
                    SaveReport(Doc, OutputDir, RepName, logs)
                Catch ex As Exception
                    logs.AddLogText("Report Create Error for '" & RepName & "'.")
                    logs.AddLogText("Report Create Exception" & ex.ToString)
                    SaveReport(Doc, OutputDir, RepName, logs)
                End Try

            End If
        Next mt_count

    End Sub
    Sub SaveReport(ByRef Doc As busobj.Document, ByRef OutputDir As String, ByRef RepName As String, ByRef logs As logMessages)
        Dim dp As busobj.DataProvider
        Dim Location As String

        If OutputDir <> "" Then
            Location = OutputDir & "\"
        End If
        Try
            If OutputDir <> "" Then
                Doc.SaveAs(Location & RepName, True)
            Else
                Doc.SaveAs(RepName, True)
            End If
            Doc.Close()
        Catch ex As Exception
            logs.AddLogText("Report '" & RepName & "' saving to '" & Location & "' failed.")
            logs.AddLogText("Data provider exception: " & ex.ToString)
        End Try
    End Sub
    Function loadDataProvider(ByRef BoApp As busobj.Application, ByRef Doc As busobj.Document, ByRef Name As String, ByRef logs As logMessages, ByRef UniverseExtension As String) As busobj.DataProvider
        Dim data_prov As busobj.DataProvider
        Dim Univ As busobj.Universe
        Dim UniverseFolder As String
        UniverseFolder = ""
        Try
            'For Each Univ In BoApp.Universes
            'Try
            'If Univ.LongName = UniverseName Then
            'UniverseFolder = Univ.DomainName
            'Exit For
            'End If
            'Catch ex As Exception
            'logs.AddLogText("Data provider exception: " & ex.ToString)
            'Return Nothing
            'End Try
            'Next
            If UniverseExtension = "" Then
                data_prov = Doc.DataProviders.AddQueryTechnique(UniverseName, "Universe")
            Else
                data_prov = Doc.DataProviders.AddQueryTechnique(UniverseName & " " & UniverseExtension, "Universe")
            End If

            data_prov.IsRefreshable = False
            'dp.Name = Name
            data_prov.Load()
        Catch ex As Exception
            logs.AddLogText("Data provider creation from universe '" & UniverseName & " " & UniverseExtension & "' failed.")
            logs.AddLogText("Data provider exception: " & ex.ToString)
            Return Nothing
        End Try
        Return data_prov
    End Function
    Sub unloadDataProvider(ByRef Name As String, ByRef data_prov As busobj.DataProvider, ByRef logs As logMessages)
        Try
            data_prov.Unload()
            data_prov.IsRefreshable = True
            data_prov.Name = Name
            'MsgBox(data_prov.SQL)
        Catch ex As Exception
            logs.AddLogText("Data provider unload: " & ex.ToString)
            Exit Sub
        End Try
    End Sub
    Sub applyReportSettings(ByRef Doc As busobj.Document, ByRef rep As busobj.Report, ByRef template As String, ByRef RepName As String, ByRef logs As logMessages)
        Try
            rep.ApplyTemplate(template)
            Doc.DocumentVariables("Report Title").Formula = RepName
            rep.Name = RepName
            Doc.Title = RepName
        Catch ex As Exception
            logs.AddLogText("Verification report '" & RepName & "' with template '" & template & "' naming failed.")
            logs.AddLogText("Verification report exception:" & ex.ToString)
            Exit Sub
        End Try
    End Sub

    Sub VerifReports_makeVerificationReport_Day(ByRef BoApp As busobj.Application, ByRef OutputDir As String, ByRef logs As logMessages, ByRef check As Boolean, ByRef mts As MeasurementTypes, ByRef UniverseExtension As String)

        Dim Doc As busobj.Document
        Dim unvObj As busobj.Universe
        Dim claObj As busobj.Class
        Dim claObj2 As busobj.Class
        Dim claObj3 As busobj.Class
        Dim claObj4 As busobj.Class
        Dim dp As busobj.DataProvider
        Dim dp2 As busobj.DataProvider
        Dim rep As busobj.Report
        Dim RepName As String
        Dim count As Integer
        Dim CreateReport As Boolean

        'Make Total Verification reports
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" And mt.DayAggregation = True Then
                If check = False Then
                    CreateReport = True
                End If
                If check = True Then
                    If MsgBox("Do you want to create DAY Total Verification report for " & mt.MeasurementTypeID & "? ", MsgBoxStyle.YesNo) = MsgBoxResult.Yes Then
                        CreateReport = True
                    Else
                        CreateReport = False
                    End If
                End If

                If CreateReport = True Then
                    Doc = BoApp.Documents.Add 'Add new document
                    dp = loadDataProvider(BoApp, Doc, "RAW", logs, UniverseExtension)
                    If dp Is Nothing Then
                        Exit Sub
                    End If
                    unvObj = dp.Universe 'Set universe
                    claObj = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Counters", "", logs, mts) 'Get measurement objects
                    claObj2 = VerifReports_ClassObject(claObj, mt.MeasurementTypeID, "", logs, mts) 'Get measurement objects
                    VerifReports_AddObjects(dp, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add objects
                    VerifReports_AddClassKeyObjects(dp, claObj2, logs) 'Add measurement keys
                    VerifReports_AddClassObjects(dp, claObj, mt.MeasurementTypeID, mt.Counters, "", False, logs) 'Add measurement counters
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "TOPOLOGY", logs) 'Add topology conditions
                    VerifReports_AddKeyConditions(Doc, dp, mt.MeasurementTypeID, "KEYTOPOLOGY", "", logs) 'Add topology conditions
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add time conditions
                    unloadDataProvider("RAW", dp, logs)

                    dp2 = loadDataProvider(BoApp, Doc, "DAY", logs, UniverseExtension)
                    If dp2 Is Nothing Then
                        Exit Sub
                    End If
                    VerifReports_AddObjects(dp2, mt.MeasurementTypeID, "TOTAL_DAY", logs) 'Add objects
                    VerifReports_AddClassKeyObjects(dp2, claObj2, logs) 'Add measurement keys
                    VerifReports_AddClassObjects(dp2, claObj, mt.MeasurementTypeID, mt.Counters, "", False, logs) 'Add measurement counters
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "TOPOLOGY", logs) 'Add topology conditions
                    VerifReports_AddKeyConditions(Doc, dp2, mt.MeasurementTypeID, "KEYTOPOLOGY", "", logs) 'Add topology conditions
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "TOTAL_DAY", logs) 'Add time conditions
                    unloadDataProvider("DAY", dp2, logs)

                    'Add new report
                    RepName = "Verification_" & claObj.Name
                    Try
                        rep = Doc.Reports.CreateQuickReport(dp.Name)
                        applyReportSettings(Doc, rep, "Verification_Template.ret", RepName, logs)
                        VerifReports_BuildRawReportTables(Doc, rep, logs)
                        VerifReports_BuildTotalReportTables(Doc, rep, dp2, logs) 'Fill total report tables
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "RAW", mt.Counters)
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "DAY", mt.Counters)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    Catch ex As Exception
                        logs.AddLogText("Report Create Error for '" & RepName & "'. Check report object and conditions for levels TOTAL_RAW, TOTAL_DAY, TOPOLOGY and KEYTOPOLOGY")
                        logs.AddLogText("Report Create Exception" & ex.ToString)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    End Try
                End If

            End If
        Next mt_count

    End Sub
    Sub VerifReports_makeVerificationReport_Count(ByRef BoApp As busobj.Application, ByRef OutputDir As String, ByRef logs As logMessages, ByRef check As Boolean, ByRef mts As MeasurementTypes, ByRef UniverseExtension As String)

        Dim Doc As busobj.Document
        Dim unvObj As busobj.Universe
        Dim claObj As busobj.Class
        Dim claObj2 As busobj.Class
        Dim claObj3 As busobj.Class
        Dim claObj4 As busobj.Class
        Dim dp As busobj.DataProvider
        Dim dp2 As busobj.DataProvider
        Dim rep As busobj.Report
        Dim RepName As String
        Dim CreateReport As Boolean

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" And mt.CreateCountTable = True Then

                If check = False Then
                    CreateReport = True
                End If
                If check = True Then
                    If MsgBox("Do you want to create COUNT Total Verification report for " & mt.MeasurementTypeID & "? ", MsgBoxStyle.YesNo) = MsgBoxResult.Yes Then
                        CreateReport = True
                    Else
                        CreateReport = False
                    End If
                End If

                If CreateReport = True Then
                    Doc = BoApp.Documents.Add 'Add new document
                    dp = loadDataProvider(BoApp, Doc, "RAW", logs, UniverseExtension)

                    If dp Is Nothing Then
                        Exit Sub
                    End If
                    unvObj = dp.Universe 'Set universe
                    claObj3 = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Counters", "_RAW", logs, mts) 'Get measurement objects
                    claObj4 = VerifReports_ClassObject(claObj3, mt.MeasurementTypeID, "_RAW", logs, mts) 'Get measurement objects
                    VerifReports_AddObjects(dp, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add objects
                    VerifReports_AddClassKeyObjects(dp, claObj4, logs) 'Add measurement keys
                    VerifReports_AddClassObjects(dp, claObj3, mt.MeasurementTypeID, mt.Counters, "_RAW", True, logs) 'Add measurement counters
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "TOPOLOGY", logs) 'Add topology conditions
                    VerifReports_AddKeyConditions(Doc, dp, mt.MeasurementTypeID, "KEYTOPOLOGY", "_RAW", logs) 'Add topology conditions
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add time conditions
                    unloadDataProvider("RAW", dp, logs)

                    dp2 = loadDataProvider(BoApp, Doc, "DAY", logs, UniverseExtension)
                    If dp2 Is Nothing Then
                        Exit Sub
                    End If
                    claObj = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Counters", "", logs, mts) 'Get measurement objects
                    claObj2 = VerifReports_ClassObject(claObj, mt.MeasurementTypeID, "", logs, mts) 'Get measurement objects
                    VerifReports_AddObjects(dp2, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add objects
                    VerifReports_AddClassKeyObjects(dp2, claObj2, logs) 'Add measurement keys
                    VerifReports_AddClassObjects(dp2, claObj, mt.MeasurementTypeID, mt.Counters, "", True, logs) 'Add measurement counters
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "TOPOLOGY", logs) 'Add topology conditions
                    VerifReports_AddKeyConditions(Doc, dp2, mt.MeasurementTypeID, "KEYTOPOLOGY", "", logs) 'Add topology conditions
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "TOTAL_RAW", logs) 'Add time conditions
                    unloadDataProvider("DAY", dp2, logs)

                    RepName = "Verification_" & claObj.Name & "_COUNT"
                    Try
                        rep = Doc.Reports.CreateQuickReport(dp.Name)
                        applyReportSettings(Doc, rep, "Verification_Count_Template.ret", RepName, logs)
                        VerifReports_BuildTotalReportTables(Doc, rep, dp2, logs) 'Fill total report tables
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "RAW", mt.Counters)
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "DAY", mt.Counters)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    Catch ex As Exception
                        logs.AddLogText("Report Create Error for '" & RepName & "'.")
                        logs.AddLogText("Report Create Error for '" & RepName & "'. Check report object and conditions for levels TOTAL_RAW, TOPOLOGY and KEYTOPOLOGY")
                        logs.AddLogText("Report Create Exception" & ex.ToString)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    End Try
                End If
            End If
        Next mt_count

    End Sub
    Sub VerifReports_makeVerificationReport_DayBH(ByRef BoApp As busobj.Application, ByRef OutputDir As String, ByRef logs As logMessages, ByRef check As Boolean, ByRef mts As MeasurementTypes, ByRef UniverseExtension As String)

        Dim Doc As busobj.Document
        Dim unvObj As busobj.Universe
        Dim claObj As busobj.Class
        Dim claObj2 As busobj.Class
        Dim claObj3 As busobj.Class
        Dim claObj4 As busobj.Class
        Dim dp As busobj.DataProvider
        Dim dp2 As busobj.DataProvider
        Dim rep As busobj.Report
        Dim RepName As String
        Dim CreateReport As Boolean

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" And mt.ObjectBusyHours <> "" And mt.RankTable = False Then
                If check = False Then
                    CreateReport = True
                End If
                If check = True Then
                    If MsgBox("Do you want to create DAYBH Busy Hour Verification report for " & mt.MeasurementTypeID & "? ", MsgBoxStyle.YesNo) = MsgBoxResult.Yes Then
                        CreateReport = True
                    Else
                        CreateReport = False
                    End If
                End If

                If CreateReport = True Then
                    Doc = BoApp.Documents.Add 'Add new document

                    dp = loadDataProvider(BoApp, Doc, "RAW", logs, UniverseExtension)
                    If dp Is Nothing Then
                        Exit Sub
                    End If
                    unvObj = dp.Universe 'Set universe
                    claObj = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Counters", "", logs, mts) 'Get measurement objects
                    claObj2 = VerifReports_ClassObject(claObj, mt.MeasurementTypeID, "", logs, mts) 'Get measurement objects
                    VerifReports_AddObjects(dp, mt.MeasurementTypeID, "DAYBH_RAW", logs) 'Add objects
                    VerifReports_AddClassKeyObjects(dp, claObj2, logs) 'Add measurement keys
                    VerifReports_AddClassObjects(dp, claObj, mt.MeasurementTypeID, mt.Counters, "", False, logs) 'Add measurement counters
                    VerifReports_AddObjects(dp, mt.MeasurementTypeID, "DAYBH_RAW_BH", logs) 'Add objects
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "TOPOLOGY", logs) 'Add topology conditions
                    VerifReports_AddKeyConditions(Doc, dp, mt.MeasurementTypeID, "KEYTOPOLOGY", "", logs) 'Add topology conditions
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "DAYBH_RAW", logs) 'Add time conditions
                    unloadDataProvider("RAW", dp, logs)

                    dp2 = loadDataProvider(BoApp, Doc, "DAY", logs, UniverseExtension)
                    If dp2 Is Nothing Then
                        Exit Sub
                    End If
                    If FullAware = True Then
                        claObj = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Counters", "", logs, mts) 'Get measurement objects
                        claObj2 = VerifReports_ClassObject(claObj, mt.MeasurementTypeID, "", logs, mts) 'Get measurement objects
                    Else
                        claObj = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Counters", "_BH", logs, mts) 'Get measurement objects
                        claObj2 = VerifReports_ClassObject(claObj, mt.MeasurementTypeID, "_BH", logs, mts) 'Get measurement objects
                    End If

                    VerifReports_AddObjects(dp2, mt.MeasurementTypeID, "DAYBH_DAY", logs) 'Add objects
                    VerifReports_AddClassKeyObjects(dp2, claObj2, logs) 'Add measurement keys
                    If FullAware = True Then
                        VerifReports_AddClassObjects(dp2, claObj, mt.MeasurementTypeID, mt.Counters, "", False, logs) 'Add measurement counters
                    Else
                        VerifReports_AddClassBHObjects(dp2, claObj, mt.MeasurementTypeID, mt.Counters, logs) 'Add measurement counters
                    End If
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "TOPOLOGY", logs) 'Add topology conditions
                    If FullAware = True Then
                        VerifReports_AddKeyConditions(Doc, dp2, mt.MeasurementTypeID, "KEYTOPOLOGY", "", logs) 'Add topology conditions
                    Else
                        VerifReports_AddKeyConditions(Doc, dp2, mt.MeasurementTypeID, "KEYTOPOLOGY", "_BH", logs) 'Add topology conditions
                    End If
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "DAYBH", logs) 'Add time conditions
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "DAYBH_DAY", logs) 'Add time conditions
                    unloadDataProvider("DAY", dp2, logs)

                    RepName = "Verification_" & claObj.Name & "_BH"
                    Try
                        rep = Doc.Reports.CreateQuickReport(dp.Name)
                        applyReportSettings(Doc, rep, "Verification_BH_Template.ret", RepName, logs)
                        VerifReports_BuildBHReportTables(Doc, rep, dp2, logs) 'Fill tables
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "RAW", mt.Counters)
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "DAY", mt.Counters)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    Catch ex As Exception
                        logs.AddLogText("Report Create Error for '" & RepName & "'.")
                        logs.AddLogText("Report Create Error for '" & RepName & "'. Check report object and conditions for levels DAYBH_RAW, TOPOLOGY and KEYTOPOLOGY")
                        logs.AddLogText("Report Create Exception" & ex.ToString)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    End Try

                End If

            End If
        Next mt_count

    End Sub
    Sub VerifReports_makeVerificationReport_ElemBH(ByRef BoApp As busobj.Application, ByRef OutputDir As String, ByRef logs As logMessages, ByRef check As Boolean, ByRef mts As MeasurementTypes, ByRef UniverseExtension As String)

        Dim Doc As busobj.Document
        Dim unvObj As busobj.Universe
        Dim claObj As busobj.Class
        Dim claObj2 As busobj.Class
        Dim claObj3 As busobj.Class
        Dim claObj4 As busobj.Class
        Dim dp As busobj.DataProvider
        Dim dp2 As busobj.DataProvider
        Dim rep As busobj.Report
        Dim RepName As String
        Dim i As Integer
        Dim CreateReport As Boolean

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" And mt.ElementBusyHours = True And mt.RankTable = False Then
                If check = False Then
                    CreateReport = True
                End If
                If check = True Then
                    If MsgBox("Do you want to create ELEMBH Busy Hour Verification report for " & mt.MeasurementTypeID & "? ", MsgBoxStyle.YesNo) = MsgBoxResult.Yes Then
                        CreateReport = True
                    Else
                        CreateReport = False
                    End If
                End If

                If CreateReport = True Then
                    Doc = BoApp.Documents.Add 'Add new document

                    dp = loadDataProvider(BoApp, Doc, "RAW", logs, UniverseExtension)
                    If dp Is Nothing Then
                        Exit Sub
                    End If
                    unvObj = dp.Universe 'Set universe
                    claObj = VerifReports_ClassObject(unvObj, mt.MeasurementTypeID, "Counters", "", logs, mts) 'Get measurement objects
                    If claObj Is Nothing Then
                        Exit For
                    End If
                    claObj2 = VerifReports_ClassObject(claObj, mt.MeasurementTypeID, "", logs, mts) 'Get measurement objects
                    If claObj2 Is Nothing Then
                        Exit For
                    End If
                    VerifReports_AddObjects(dp, mt.MeasurementTypeID, "ELEM_RAW", logs) 'Add objects
                    VerifReports_AddClassObjects(dp, claObj, mt.MeasurementTypeID, mt.Counters, "", False, logs) 'Add measurement counters
                    VerifReports_AddObjects(dp, mt.MeasurementTypeID, "ELEM_RAW_BH", logs) 'Add objects
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "DAYBH_RAW", logs) 'Add time conditions
                    VerifReports_AddConditions(Doc, dp, mt.MeasurementTypeID, "ELEMBH_RAW", logs) 'Add time conditions
                    unloadDataProvider("RAW", dp, logs)

                    dp2 = loadDataProvider(BoApp, Doc, "DAY", logs, UniverseExtension)
                    If dp2 Is Nothing Then
                        Exit Sub
                    End If
                    VerifReports_AddObjects(dp2, mt.MeasurementTypeID, "ELEM_DAY", logs) 'Add objects
                    VerifReports_AddClassObjects(dp2, claObj, mt.MeasurementTypeID, mt.Counters, "", False, logs) 'Add measurement counters
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "DAYBH", logs)
                    VerifReports_AddConditions(Doc, dp2, mt.MeasurementTypeID, "ELEMBH_DAY", logs) 'Add time conditions
                    unloadDataProvider("DAY", dp2, logs)

                    RepName = "Verification_" & claObj.Name & "_ELEMBH"
                    Try
                        rep = Doc.Reports.CreateQuickReport(dp.Name)
                        applyReportSettings(Doc, rep, "Verification_BH_Template.ret", RepName, logs)
                        VerifReports_BuildBHReportTables(Doc, rep, dp2, logs) 'Fill tables
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "RAW", mt.Counters)
                        VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "DAY", mt.Counters)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    Catch ex As Exception
                        logs.AddLogText("Report Create Error for '" & RepName & "'.")
                        logs.AddLogText("Report Create Error for '" & RepName & "'. Check report object and conditions for levels ELEM_RAW and DAYBH_RAW")
                        logs.AddLogText("Report Create Exception" & ex.ToString)
                        SaveReport(Doc, OutputDir, RepName, logs)
                    End Try

                End If
            End If
        Next mt_count

    End Sub
    Private Function VerifReports_AddObjects(ByRef data_provider As busobj.DataProvider, ByRef Measurement As String, ByRef Level As String, ByRef logs As logMessages) As Object

        Dim Measurements() As String
        Dim meas_count As Integer
        Dim count As Integer

        'Make Total Verification reports
        For count = 1 To repobjs.Count
            repobj = repobjs.Item(count)
            Measurements = Split(repobj.MeasurementTypeID, ",")
            For meas_count = 0 To UBound(Measurements)
                If (Measurements(meas_count) = Measurement Or Measurements(meas_count) = "All") And repobj.Level = Level Then
                    addResultObject(data_provider, repobj.ObjectClass, repobj.Name, logs)
                End If
            Next meas_count
        Next count

    End Function
    Private Function VerifReports_AddConditions(ByRef Doc As busobj.Document, ByRef data_provider As busobj.DataProvider, ByRef Measurement As String, ByRef Level As String, ByRef logs As logMessages) As Object

        Dim Measurements() As String
        Dim meas_count As Integer
        Dim count As Integer
        Dim Var As busobj.Variable

        For count = 1 To repconds.Count
            repcond = repconds.Item(count)
            Measurements = Split(repcond.MeasurementTypeID, ",")
            For meas_count = 0 To UBound(Measurements)
                If Measurements(meas_count) = "All" And repcond.Level = Level And repcond.ObjectCondition <> "yes" Then
                    addConditionObject(data_provider, Doc, repcond.CondClass, repcond.Name, logs)
                End If
                If Measurements(meas_count) = Measurement And repcond.Level = Level And repcond.ObjectCondition <> "yes" Then
                    addConditionObject(data_provider, Doc, repcond.CondClass, repcond.Name, logs)
                End If
                If (Measurements(meas_count) = Measurement Or Measurements(meas_count) = "All") And repcond.Level = Level And repcond.ObjectCondition = "yes" Then
                    Var = Doc.Variables.Add(repcond.Prompt1 & ":")
                    Try
                        data_provider.Queries.Item(1).Conditions.Add(repcond.CondClass, repcond.Name, "Equal to", (repcond.Prompt1 & ":"), "Prompt")
                    Catch ex As Exception
                        logs.AddLogText("Report Condition '" & repcond.CondClass & "/" & repcond.Name & "' adding in data provider '" & data_provider.Name & "' failed.")
                        logs.AddLogText("Report Condition Exception: " & ex.ToString)
                        Exit Function
                    End Try
                End If
            Next meas_count
        Next count

    End Function
    Private Function VerifReports_AddClassObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As busobj.Class, ByRef Measurement As String, ByRef mt_cnts As Counters, ByRef Annex As String, ByRef CountRep As Boolean, ByRef logs As logMessages) As Object

        Dim ClassTree() As String
        Dim TreeCount As Integer
        Dim objectName As String
        Dim count As Integer

        For cnt_count = 1 To mt_cnts.Count
            cnt = mt_cnts.Item(cnt_count)

            If cnt.MeasurementTypeID = Measurement And cnt.UnivObject <> "" Then
                If cnt.oneAggrFormula = True And cnt.oneAggrValue <> "" Then
                    If cnt.oneAggrValue <> "SUM" Then
                        objectName = cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")"
                    Else
                        objectName = cnt.UnivObject
                    End If
                    If cnt.UnivClass <> "" Then
                        ClassTree = Split(cnt.UnivClass, "//")
                        TreeCount = UBound(ClassTree)
                        addResultObject(data_provider, ClassTree(TreeCount) & Annex, objectName, logs)
                    Else
                        addResultObject(data_provider, cnt.MeasurementTypeID & Annex, objectName, logs)
                    End If
                ElseIf cnt.oneAggrFormula = False Then
                    If cnt.UnivClass <> "" Then
                        ClassTree = Split(cnt.UnivClass, "//")
                        TreeCount = UBound(ClassTree)
                        For count = 0 To UBound(cnt.Aggregations)
                            addResultObject(data_provider, ClassTree(TreeCount) & Annex, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", logs)
                        Next count
                    Else
                        For count = 0 To UBound(cnt.Aggregations)
                            addResultObject(data_provider, cnt.MeasurementTypeID & Annex, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", logs)
                        Next count
                    End If
                Else
                    logs.AddLogText("Report Result Object '" & cnt.UnivObject & "' adding for Fact Table '" & cnt.MeasurementTypeID & "' in data provider '" & data_provider.Name & "' failed.")
                End If
            End If
        Next cnt_count
        If CountRep = False Then
            addResultObject(data_provider, Measurement & Annex, "data_coverage", logs)
        End If
        addResultObject(data_provider, Measurement & Annex, "period_duration", logs)


    End Function
    Private Function VerifReports_AddClassBHObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As busobj.Class, ByRef Measurement As String, ByRef mt_cnts As Counters, ByRef logs As logMessages) As Object

        Dim ClassTree() As String
        Dim TreeCount As Integer
        Dim objectName As String
        Dim count As Integer

        For cnt_count = 1 To mt_cnts.Count
            cnt = mt_cnts.Item(cnt_count)

            If cnt.MeasurementTypeID = Measurement And cnt.UnivObject <> "" Then
                If cnt.oneAggrFormula = True And cnt.oneAggrValue <> "" Then
                    If cnt.oneAggrValue <> "SUM" Then
                        objectName = cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")"
                    Else
                        objectName = cnt.UnivObject
                    End If
                    If cnt.UnivClass <> "" Then
                        ClassTree = Split(cnt.UnivClass, "//")
                        TreeCount = UBound(ClassTree)
                        addResultObject(data_provider, ClassTree(TreeCount) & "_BH", objectName, logs)
                    Else
                        addResultObject(data_provider, cnt.MeasurementTypeID & "_BH", objectName, logs)
                    End If
                ElseIf cnt.oneAggrFormula = False And cnt.oneAggrValue <> "" Then
                    If cnt.UnivClass <> "" Then
                        ClassTree = Split(cnt.UnivClass, "//")
                        TreeCount = UBound(ClassTree)
                        For count = 0 To UBound(cnt.Aggregations)
                            addResultObject(data_provider, ClassTree(TreeCount) & "_BH", cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", logs)
                        Next count
                    Else
                        For count = 0 To UBound(cnt.Aggregations)
                            addResultObject(data_provider, cnt.MeasurementTypeID & "_BH", cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", logs)
                        Next count
                    End If

                Else
                    logs.AddLogText("Report Result Object '" & cnt.UnivObject & "' adding for Fact Table '" & cnt.MeasurementTypeID & "' in data provider '" & data_provider.Name & "' failed.")
                End If
            End If
        Next cnt_count
        addResultObject(data_provider, Measurement & "_BH", "period_duration", logs)

    End Function
    Private Function VerifReports_AddClassKeyObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As busobj.Class, ByRef logs As logMessages) As Object

        Dim Inx As Short

        'Make Total Verification reports
        For Inx = 1 To class_obj.Objects.Count
            If class_obj.Objects.Item(Inx).Name <> "hours from now" And class_obj.Objects.Item(Inx).Name <> "datetime (raw)" And class_obj.Objects.Item(Inx).Name <> "Datetime (UTC)" Then
                addResultObject(data_provider, class_obj.Name, class_obj.Objects.Item(Inx).Name, logs)
            End If
        Next

    End Function
    Sub addConditionObject(ByRef data_provider As busobj.DataProvider, ByRef Doc As busobj.Document, ByRef ClassName As String, ByRef ConditionName As String, ByRef logs As logMessages)
        Try
            data_provider.Queries.Item(1).Conditions.Add(ClassName, ConditionName)

            If repcond.Prompt1 <> "" Then
                Doc.Variables.Item(repcond.Prompt1 & ":").Value = repcond.Value1
            End If
            If repcond.Prompt2 <> "" Then
                Doc.Variables.Item(repcond.Prompt2 & ":").Value = repcond.Value2
            End If
            If repcond.Prompt3 <> "" Then
                Doc.Variables.Item(repcond.Prompt2 & ":").Value = repcond.Value3
            End If
        Catch ex As Exception
            logs.AddLogText("Report Condition '" & ClassName & "/" & ConditionName & "' adding in data provider '" & data_provider.Name & "' failed.")
            logs.AddLogText("Report Condition Exception: " & ex.ToString)
            Exit Sub
        End Try
    End Sub
    Sub addResultObject(ByRef data_provider As busobj.DataProvider, ByRef ClassName As String, ByRef ObjectName As String, ByRef logs As logMessages)
        Try
            data_provider.Queries.Item(1).Results.Add(ClassName, ObjectName)
        Catch ex As Exception
            logs.AddLogText("Report Result '" & ClassName & "/" & ObjectName & "' adding in data provider '" & data_provider.Name & "' failed.")
            logs.AddLogText("Report Result Exception: " & ex.ToString)
            Exit Sub
        End Try
    End Sub
    Private Function VerifReports_AddCMClassObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As busobj.Class, ByRef Measurement As String, ByRef mt_cnts As Counters, ByRef logs As logMessages) As Object

        Dim ClassTree() As String
        Dim TreeCount As Integer
        Dim first As Boolean
        Dim tempAggregations As String
        Dim Aggregations() As String
        Dim oneAggrValue As String
        Dim oneAggrFormula As Boolean
        Dim count As Integer

        For cnt_count = 1 To mt_cnts.Count
            cnt = mt_cnts.Item(cnt_count)

            'get different aggregation formulas
            first = True
            tempAggregations = ""
            oneAggrFormula = False
            oneAggrValue = ""
            For count = 0 To UBound(cnt.TimeAggrList)
                If InStrRev(tempAggregations, cnt.TimeAggrList(count)) = 0 Then
                    If first = False Then
                        tempAggregations &= ","
                    End If
                    If first = True Then
                        first = False
                    End If
                    tempAggregations &= cnt.TimeAggrList(count)
                End If
            Next count
            For count = 0 To UBound(cnt.GroupAggrList)
                If InStrRev(tempAggregations, cnt.GroupAggrList(count)) = 0 Then
                    If first = False Then
                        tempAggregations &= ","
                    End If
                    If first = True Then
                        first = False
                    End If
                    tempAggregations &= cnt.GroupAggrList(count)
                End If
            Next count
            Aggregations = Split(tempAggregations, ",")
            If UBound(Aggregations) = 0 Then
                oneAggrFormula = True
                oneAggrValue = Aggregations(0)
            End If

            If cnt.MeasurementTypeID = Measurement And cnt.UnivObject <> "" Then
                If oneAggrFormula = True And oneAggrValue <> "" Then
                    If cnt.UnivClass <> "" Then
                        ClassTree = Split(cnt.UnivClass, "//")
                        TreeCount = UBound(ClassTree)
                        addResultObject(data_provider, ClassTree(TreeCount), cnt.UnivObject, logs)
                    Else
                        addResultObject(data_provider, cnt.MeasurementTypeID, cnt.UnivObject, logs)
                    End If
                End If
            End If
        Next cnt_count

    End Function
    Private Function VerifReports_AddKeyConditions(ByRef Doc As busobj.Document, ByRef data_provider As busobj.DataProvider, ByRef Measurement As String, ByRef Level As String, ByRef Annex As String, ByRef logs As logMessages) As Object

        Dim Measurements() As String
        Dim meas_count As Integer
        Dim count As Integer
        Dim Var As busobj.Variable

        For count = 1 To repconds.Count
            repcond = repconds.Item(count)
            Measurements = Split(repcond.MeasurementTypeID, ",")
            For meas_count = 0 To UBound(Measurements)
                If (Measurements(meas_count) = "All" Or Measurements(meas_count) = Measurement) And repcond.Level = Level Then
                    addConditionObject(data_provider, Doc, Measurement & Annex & "_Keys", repcond.Name, logs)
                End If
            Next meas_count
        Next count

    End Function
    Private Function VerifReports_ClassObject(ByRef unvObj As busobj.Universe, ByRef Measurement As String, ByRef ClassName As String, ByRef postFix As String, ByRef logs As logMessages, ByRef mts As MeasurementTypes) As busobj.Class
        Dim TestClass As busobj.Class
        Dim claObj As busobj.Class
        Dim claObj2 As busobj.Class

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID = Measurement Then
                Try
                    TestClass = unvObj.Classes.Item(ClassName).Classes.Item(mt.MeasurementTypeClassDescription & " " & ClassName).Classes.Item(Measurement & postFix)
                Catch ex As Exception
                    logs.AddLogText("Class '" & mt.MeasurementTypeClassDescription & " " & ClassName & "/" & Measurement & postFix & "' not found in universe.")
                    logs.AddLogText("Class Exception: " & ex.ToString)
                End Try
                Exit For
            End If
        Next mt_count

        If TestClass Is Nothing Then
            logs.AddLogText("Class '" & mt.MeasurementTypeClassDescription & " " & ClassName & "/" & Measurement & postFix & "' not found in universe.")
        End If
        Return TestClass
    End Function

    Private Function VerifReports_ClassObject(ByRef unvClass As busobj.Class, ByRef Measurement As String, ByRef postFix As String, ByRef logs As logMessages, ByRef mts As MeasurementTypes) As busobj.Class
        Dim TestClass As busobj.Class

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID = Measurement Then
                Try
                    TestClass = unvClass.Classes.Item(Measurement & postFix & "_Keys")
                Catch ex As Exception
                    logs.AddLogText("Class '" & unvClass.Name & "/" & Measurement & postFix & "_Keys" & "' not found in universe.")
                    logs.AddLogText("Class Exception: " & ex.ToString)
                End Try
                Exit For
            End If
        Next mt_count

        If TestClass Is Nothing Then
            logs.AddLogText("Class '" & unvClass.Name & "/" & Measurement & postFix & "_Keys" & "' not found in universe.")
        End If

        Return TestClass
    End Function

    Private Function VerifReports_FormatColumns(ByRef Doc As busobj.Document, ByRef Measurement As String, ByRef DataProv As String, ByRef cnts As Counters) As Object

        Dim DocVar As busobj.DocumentVariable
        Dim i As Short
        Dim intSubStringLoc As Short
        Dim intSubStringLoc2 As Short
        Dim tempHeader As String

        Dim ObjRowCount As Short

        Dim ColNames() As String

        For i = 1 To Doc.DocumentVariables.Count
            If InStrRev(Doc.DocumentVariables(i).Formula, "=NameOf(<") > 0 AndAlso InStrRev(Doc.DocumentVariables(i).Formula, "(" & DataProv & ")>") > 0 Then
                intSubStringLoc = InStr(Doc.DocumentVariables(i).Formula, "=NameOf(<")
                intSubStringLoc2 = InStr(Doc.DocumentVariables(i).Formula, "(" & DataProv & ")>")
                tempHeader = Mid(Doc.DocumentVariables(i).Formula, intSubStringLoc + 9, intSubStringLoc2 - (intSubStringLoc + 9))
                Doc.DocumentVariables(i).Formula = tempHeader

                For cnt_count = 1 To cnts.Count
                    cnt = cnts.Item(cnt_count)

                    ColNames = Split(tempHeader, " ")

                    If cnt.MeasurementTypeID = Measurement AndAlso StrComp(ColNames(0), cnt.UnivObject) = 0 AndAlso cnt.UnivObject <> "" Then
                        Doc.DocumentVariables(i).Formula = tempHeader
                        Exit For
                    End If
                Next cnt_count
            End If
        Next i
    End Function
    Sub VerifReports_BuildRawReportTables(ByRef document As busobj.Document, ByRef report As busobj.Report, ByRef logs As logMessages)

        Dim boRepStrucItem As busobj.ReportStructureItem
        Dim boRepStrucItems As busobj.ReportStructureItems

        Dim boRepFooterItem As busobj.ReportStructureItem
        Dim boRepFooterItems As busobj.ReportStructureItems

        Dim boBlockStruc As busobj.BlockStructure
        Dim boPiv As busobj.Pivot
        Dim boSecStruc As busobj.SectionStructure

        Dim i As Object
        Dim j As Short

        boSecStruc = report.GeneralSectionStructure
        boRepStrucItems = boSecStruc.Body

        ' Loop through all of the variables on the report
        For i = 1 To boRepStrucItems.Count
            boRepStrucItem = boRepStrucItems.Item(i)
            'If the report structure object is a table
            If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
                boBlockStruc = boRepStrucItem
                ' If Table is for raw counters (Table 1)
                If boBlockStruc.Name = "Table 1" Then
                    boPiv = boBlockStruc.Pivot

                    'remove "dummy" object from the table
                    For j = 1 To boPiv.BodyCount
                        If boPiv.Body(j).Name = "dummy" Then
                            boPiv.Body(j).Delete()
                            boPiv.Apply()
                        End If
                    Next j
                End If
            End If
        Next i

        boRepStrucItem = Nothing
        boRepStrucItems = Nothing
        boBlockStruc = Nothing
        boPiv = Nothing

    End Sub
    Sub VerifReports_BuildTotalReportTables(ByRef document As busobj.Document, ByRef report As busobj.Report, ByRef d_prv As busobj.DataProvider, ByRef logs As logMessages)

        Dim boRepStrucItem As busobj.ReportStructureItem
        Dim boRepStrucItems As busobj.ReportStructureItems

        Dim boRepFooterItem As busobj.ReportStructureItem
        Dim boRepFooterItems As busobj.ReportStructureItems

        Dim boBlockStruc As busobj.BlockStructure
        Dim boPiv As busobj.Pivot
        Dim boSecStruc As busobj.SectionStructure

        Dim i As Object
        Dim j As Short

        boSecStruc = report.GeneralSectionStructure
        boRepStrucItems = boSecStruc.Body

        ' Loop through all of the variables on the report
        For i = 1 To boRepStrucItems.Count
            boRepStrucItem = boRepStrucItems.Item(i)
            'If the report structure object is a table
            If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
                boBlockStruc = boRepStrucItem
                ' If Table is for day counters (Table 1.0)
                If boBlockStruc.Name = "Table 1.0" Then
                    boPiv = boBlockStruc.Pivot
                    'Add day counters to table
                    For j = 1 To d_prv.Columns.Count
                        Try
                            If InStrRev(d_prv.Columns(j).Name, "(none)") = 0 Then
                                boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name + "(DAY)")
                            End If
                        Catch ex As Exception
                            logs.AddLogText("Error in '" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "'. Retrying.")
                            Try
                                boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name)
                                logs.AddLogText("'" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "' retry successful.")
                            Catch e As Exception
                                logs.AddLogText("Error in '" & d_prv.Columns(j).Name & "' at '" & d_prv.Name & "'.")
                                logs.AddLogText("Class Exception: " & e.ToString)
                            End Try
                        End Try
                    Next j
                    boPiv.Apply()
                End If

                If boBlockStruc.Name = "Table 1.1" Then
                    boPiv = boBlockStruc.Pivot
                    'Add day counters to table
                    For j = 1 To d_prv.Columns.Count
                        Try
                            If InStrRev(d_prv.Columns(j).Name, "(none)") = 0 Then
                                boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name + "(RAW)")
                                'MsgBox(boPiv.Body(j).Formula)
                            End If
                        Catch ex As Exception
                            logs.AddLogText("Error in '" & d_prv.Columns(j).Name & "(RAW)' at '" & d_prv.Name & "'.")
                            logs.AddLogText("Class Exception: " & ex.ToString)
                        End Try
                    Next j
                    boPiv.Apply()
                End If
            End If
        Next i

        boRepStrucItem = Nothing
        boRepStrucItems = Nothing
        boBlockStruc = Nothing
        boPiv = Nothing

    End Sub
    Sub VerifReports_BuildBHReportTables(ByRef document As busobj.Document, ByRef report As busobj.Report, ByRef d_prv As busobj.DataProvider, ByRef logs As logMessages)

        Dim boRepStrucItem As busobj.ReportStructureItem
        Dim boRepStrucItems As busobj.ReportStructureItems
        Dim boBlockStruc As busobj.BlockStructure
        Dim boPiv As busobj.Pivot
        Dim boSecStruc As busobj.SectionStructure
        Dim DocVar As busobj.DocumentVariable
        Dim Found As Boolean

        Dim i As Object
        Dim j As Short

        boSecStruc = report.GeneralSectionStructure
        boRepStrucItems = boSecStruc.Body

        ' Loop through all of the variables on the report
        For i = 1 To boRepStrucItems.Count
            boRepStrucItem = boRepStrucItems.Item(i)
            'If the report structure object is a table
            If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
                boBlockStruc = boRepStrucItem
                ' If Table is for raw counters (Table 1)
                If boBlockStruc.Name = "Table 1" Then
                    boPiv = boBlockStruc.Pivot
                    'remove "dummy" object from the table
                    For j = 1 To boPiv.BodyCount
                        If boPiv.Body(j).Name = "dummy" Then
                            boPiv.Body(j).Delete()
                            boPiv.Apply()
                            Exit For
                        End If
                    Next j

                End If
                ' If Table is for day counters (Table 1.0)
                If boBlockStruc.Name = "Table 1.0" Then
                    boPiv = boBlockStruc.Pivot
                    'Add day counters to table

                    For j = 1 To d_prv.Columns.Count
                        Try
                            If InStrRev(d_prv.Columns(j).Name, "(none)") = 0 Then
                                If d_prv.Columns(j).Name = "Busy Hour" Then
                                    boPiv.Body(j) = document.DocumentVariables("Busy Hour")
                                ElseIf d_prv.Columns(j).Name = "Element Name" Then
                                    boPiv.Body(j) = document.DocumentVariables("Element Name")
                                Else
                                    boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name & "(DAY)")
                                End If
                            End If
                        Catch ex As Exception
                            Try
                                boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name)
                            Catch e As Exception
                                logs.AddLogText("Error in '" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "'.")
                                logs.AddLogText("Class Exception: " & ex.ToString)
                                logs.AddLogText("Retry Error in '" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "'.")
                                logs.AddLogText("Class Exception: " & e.ToString)
                            End Try
                        End Try
                    Next j
                    boPiv.Apply()
                End If
            End If
        Next i


        boRepStrucItem = Nothing
        boRepStrucItems = Nothing
        boBlockStruc = Nothing
        boPiv = Nothing

    End Sub


    Private Function Universe_AddCountersDeprecated(ByRef Univ As Object, ByRef CMTechPack As Boolean, ByRef logs As logMessages) As Boolean

        Dim Cls As Designer.Class
        Dim Cls2 As Designer.Class
        Dim Cls3 As Designer.Class
        Dim Obj As Designer.Object
        'Dim Obj2 As Designer.Object
        'Dim Tbl As Designer.Table
        Dim ClassTree() As String
        Dim TreeCount As Integer
        Dim HierarchyRootClass As String
        Dim UsedClass As String
        Dim count As Integer
        Dim vectorTable As String

        Dim mts As MeasurementTypes
        Dim vector_rds As ReferenceDatas

        Dim bo_objects As New BOObjects
        Dim univ_classes As New UnivClasses

        Dim ObjNum As Designer.Object

        For mt_count = 1 To mts.Count

            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" AndAlso mt.RankTable = False Then
                If CMTechPack = True Then
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass("Parameters"), mt.MeasurementTypeClassDescription & " Parameters", mt.MeasurementTypeClassDescription & " Parameters")
                Else
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass("Counters"), mt.MeasurementTypeClassDescription & " Counters", mt.MeasurementTypeClassDescription & " Counters")
                End If
                If mt.RankTable = False AndAlso mt.CreateCountTable = True Then 'COUNT tables
                    Cls2 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID, mt.Description)
                    Cls3 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID & "_RAW", mt.Description)

                    cnts = mt.Counters
                    For cnt_count = 1 To cnts.Count
                        cnt = cnts.Item(cnt_count)

                        'get different aggregation formulas


                        Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)
                        Cls3 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW")

                        If cnt.UnivObject <> "" Then
                            UsedClass = ""
                            HierarchyRootClass = mt.MeasurementTypeID
                            If cnt.UnivClass <> "" Then
                                UsedClass = cnt.UnivClass
                                ClassTree = Split(cnt.UnivClass, "//")
                                For TreeCount = 0 To UBound(ClassTree)

                                    Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                    HierarchyRootClass = ClassTree(TreeCount)
                                    UsedClass = ClassTree(TreeCount)
                                Next TreeCount
                            End If

                            If UsedClass <> "" Then
                                Cls3 = univ_classes.addClass(Univ, Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                            End If

                            If cnt.TimeAggr = cnt.GroupAggr AndAlso cnt.TimeAggr <> "NONE" AndAlso cnt.TimeAggr <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                If cnt.TimeAggr <> "SUM" Then
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                Else
                                    Obj = bo_objects.addObject(Cls2, cnt, True, cnt.TimeAggr, logs)
                                End If
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName + ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName + ")," & (LCase(cnt.TimeAggr)) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName + ")," & (LCase(cnt.TimeAggr)) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                                If cnt.TimeAggr <> "SUM" Then
                                    Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                Else
                                    Obj = bo_objects.addObject(Cls3, cnt, True, cnt.TimeAggr, logs)
                                End If
                                If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                    Return False
                                End If
                            ElseIf cnt.TimeAggr = cnt.GroupAggr AndAlso cnt.TimeAggr = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                'Obj = bo_objects.addObject(Cls2, cnt, True, cnt.TimeAggr)
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ",DC." + cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + ")", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." + cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName + ")", logs) = False Then
                                        Return False
                                    End If
                                End If

                                Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                'Obj = bo_objects.addObject(Cls3, cnt, True, cnt.TimeAggr)
                                If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, "DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName, logs) = False Then
                                    Return False
                                End If
                            ElseIf cnt.TimeAggr <> cnt.GroupAggr AndAlso cnt.TimeAggr <> "" Then 'If Time Aggregation and Group Aggregation are different
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.GroupAggr) & ")", True, cnt.GroupAggr, logs)
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_COUNT." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                                Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                    Return False
                                End If
                                Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.GroupAggr) & ")", True, cnt.GroupAggr, logs)
                                If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                    Return False
                                End If
                            Else
                                logs.AddLogText("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.MeasurementTypeID & "' not added/modified")
                            End If
                        Else

                        End If

                        'Vector objects here
                        If cnt.CounterType = "VECTOR" Then
                            vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                            For count = 1 To vector_rds.Count
                                rd = vector_rds.Item(count)
                                If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                    Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)
                                    Cls3 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW")

                                    UsedClass = ""
                                    HierarchyRootClass = mt.MeasurementTypeID
                                    If cnt.UnivClass <> "" Then
                                        UsedClass = cnt.UnivClass
                                        ClassTree = Split(cnt.UnivClass, "//")
                                        For TreeCount = 0 To UBound(ClassTree)
                                            Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                            HierarchyRootClass = ClassTree(TreeCount)
                                            UsedClass = ClassTree(TreeCount)
                                        Next TreeCount
                                    End If

                                    If UsedClass <> "" Then
                                        Cls3 = univ_classes.addClass(Univ, Cls.Classes.FindClass(mt.MeasurementTypeID & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                                    End If

                                    Obj = bo_objects.addObject(Cls2, rd, False, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                        Return False
                                    End If
                                    Obj = bo_objects.addObject(Cls3, rd, False, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls3, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                        Return False
                                    End If
                                    'parseCounterObject(bo_objects, Obj, Cls3)
                                End If
                            Next count
                        End If
                        'Vector
                    Next cnt_count

                End If
                If mt.RankTable = False AndAlso mt.CreateCountTable = False Then 'Regular tables
                    Cls2 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID, mt.Description)

                    cnts = mt.Counters
                    For cnt_count = 1 To cnts.Count
                        cnt = cnts.Item(cnt_count)
                        Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)

                        If cnt.UnivObject <> "" Then

                            HierarchyRootClass = mt.MeasurementTypeID
                            If cnt.UnivClass <> "" Then
                                UsedClass = cnt.UnivClass
                                ClassTree = Split(cnt.UnivClass, "//")
                                For TreeCount = 0 To UBound(ClassTree)
                                    Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                    HierarchyRootClass = ClassTree(TreeCount)
                                    UsedClass = ClassTree(TreeCount)
                                Next TreeCount
                            End If

                            If cnt.TimeAggr = cnt.GroupAggr AndAlso cnt.TimeAggr <> "NONE" AndAlso cnt.TimeAggr <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                If cnt.TimeAggr <> "SUM" Then
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                Else
                                    Obj = bo_objects.addObject(Cls2, cnt, True, cnt.TimeAggr, logs)
                                End If
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                            ElseIf cnt.TimeAggr = cnt.GroupAggr AndAlso cnt.TimeAggr = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                'Obj = bo_objects.addObject(Cls2, cnt, True, cnt.TimeAggr)
                                If mt.PlainTable = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "DC." & cnt.MeasurementTypeID & "." & cnt.CounterName, logs) = False Then
                                        Return False
                                    End If
                                ElseIf CMTechPack = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName, logs) = False Then
                                        Return False
                                    End If
                                Else
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                            Return False
                                        End If
                                    Else
                                        If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ",DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & ")", logs) = False Then
                                            Return False
                                        End If
                                    End If
                                End If
                            ElseIf cnt.TimeAggr <> cnt.GroupAggr AndAlso cnt.TimeAggr <> "" Then 'If Time Aggregation and Group Aggregation are different
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.GroupAggr) & ")", True, cnt.GroupAggr, logs)
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_DAY." & cnt.CounterName & ")," & LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_RAW." & cnt.CounterName & "))", logs) = False Then
                                        Return False
                                    End If
                                End If

                            Else
                                logs.AddLogText("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.MeasurementTypeID & "' not added/modified")
                            End If
                        Else
                        End If

                        'Vector objects here
                        If cnt.CounterType = "VECTOR" Then
                            vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                            For count = 1 To vector_rds.Count
                                rd = vector_rds.Item(count)
                                If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                    Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID)

                                    HierarchyRootClass = mt.MeasurementTypeID
                                    If cnt.UnivClass <> "" Then
                                        UsedClass = cnt.UnivClass
                                        ClassTree = Split(cnt.UnivClass, "//")
                                        For TreeCount = 0 To UBound(ClassTree)
                                            Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                            HierarchyRootClass = ClassTree(TreeCount)
                                            UsedClass = ClassTree(TreeCount)
                                        Next TreeCount
                                    End If
                                    Obj = bo_objects.addObject(Cls2, rd, False, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                        Return False
                                    End If
                                End If
                            Next count
                        End If
                        'Vector
                    Next cnt_count

                End If
                'BH-classes
                If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = False Then
                    Cls2 = univ_classes.addClass(Univ, Cls, mt.MeasurementTypeID & "_BH", mt.Description)

                    cnts = mt.Counters
                    For cnt_count = 1 To cnts.Count
                        cnt = cnts.Item(cnt_count)
                        Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_BH")

                        If cnt.UnivObject <> "" Then

                            HierarchyRootClass = mt.MeasurementTypeID
                            If cnt.UnivClass <> "" Then
                                UsedClass = cnt.UnivClass
                                ClassTree = Split(cnt.UnivClass, "//")
                                For TreeCount = 0 To UBound(ClassTree)
                                    Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass & "_BH"), ClassTree(TreeCount) & "_BH", ClassTree(TreeCount) & "_BH")
                                    HierarchyRootClass = ClassTree(TreeCount)
                                    UsedClass = ClassTree(TreeCount)
                                Next TreeCount
                            End If

                            If cnt.TimeAggr = cnt.GroupAggr AndAlso cnt.TimeAggr <> "NONE" AndAlso cnt.TimeAggr <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                If cnt.TimeAggr <> "SUM" Then
                                    Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                Else
                                    Obj = bo_objects.addObject(Cls2, cnt, True, cnt.TimeAggr, logs)
                                End If
                                'Obj = bo_objects.addObject(Cls2, cnt, True, cnt.TimeAggr)
                                If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")", logs) = False Then
                                    Return False
                                End If
                            ElseIf cnt.TimeAggr = cnt.GroupAggr AndAlso cnt.TimeAggr = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                'Obj = bo_objects.addObject(Cls2, cnt, True, cnt.TimeAggr)
                                If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName, logs) = False Then
                                    Return False
                                End If
                            ElseIf cnt.TimeAggr <> cnt.GroupAggr AndAlso cnt.TimeAggr <> "" Then 'If Time Aggregation and Group Aggregation are different
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.TimeAggr) & ")", True, cnt.TimeAggr, logs)
                                If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, LCase(cnt.TimeAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." & cnt.CounterName & ")", logs) = False Then
                                    Return False
                                End If
                                Obj = bo_objects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.GroupAggr) & ")", True, cnt.GroupAggr, logs)
                                If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, LCase(cnt.GroupAggr) & "(DC." & cnt.MeasurementTypeID & "_DAYBH." + cnt.CounterName & ")", logs) = False Then
                                    Return False
                                End If
                            Else
                                logs.AddLogText("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.MeasurementTypeID & "' not added/modified")
                            End If
                        Else
                        End If

                        'Vector objects here
                        If cnt.CounterType = "VECTOR" Then
                            vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_") & "_" & cnt.CounterName
                            For count = 1 To vector_rds.Count
                                rd = vector_rds.Item(count)
                                If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                    Cls2 = Cls.Classes.FindClass(mt.MeasurementTypeID & "_BH")

                                    HierarchyRootClass = mt.MeasurementTypeID
                                    If rd.UnivClass <> "" Then
                                        UsedClass = rd.UnivClass
                                        ClassTree = Split(rd.UnivClass, "//")
                                        For TreeCount = 0 To UBound(ClassTree)
                                            Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass & "_BH"), ClassTree(TreeCount) & "_BH", ClassTree(TreeCount) & "_BH")
                                            HierarchyRootClass = ClassTree(TreeCount)
                                            UsedClass = ClassTree(TreeCount)
                                        Next TreeCount
                                    End If
                                    Obj = bo_objects.addObject(Cls2, rd, False, logs)
                                    If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID, logs) = False Then
                                        Return False
                                    End If
                                End If
                            Next count
                        End If
                        'Vector
                    Next cnt_count

                End If

            End If
        Next mt_count
        Return True

    End Function

End Class