import { Component, Inject, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';

export interface ResourceFormField {
  name: string;
  label: string;
  type: 'text' | 'number' | 'date' | 'select' | 'checkbox';
  required?: boolean;
  options?: any[];
  displayField?: string;
  valueField?: string;
}

export interface ResourceFormData {
  title: string;
  fields: ResourceFormField[];
  resource?: any;  // for editing existing resource
  additionalData?: any;  // for type-specific data like ammunition types
}

@Component({
  selector: 'app-resource-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
    MatCheckboxModule
  ],
  templateUrl: './resource-form-dialog.component.html',
  styleUrl: './resource-form-dialog.component.scss'
})
export class ResourceFormDialogComponent implements OnInit {
  form!: FormGroup;
  fb = inject(FormBuilder);

  constructor(
    public dialogRef: MatDialogRef<ResourceFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ResourceFormData
  ) {}

  ngOnInit(): void {
    this.buildForm();
  }

  buildForm(): void {
    const formControls: any = {};

    this.data.fields.forEach(field => {
      const validators = [];
      if (field.required) {
        validators.push(Validators.required);
      }
      if (field.type === 'number') {
        validators.push(Validators.min(0));
      }

      let initialValue = this.data.resource ? this.data.resource[field.name] : '';
      
      if (field.type === 'checkbox') {
        initialValue = this.data.resource ? this.data.resource[field.name] : false;
      } else if (field.type === 'date' && this.data.resource && this.data.resource[field.name]) {
        initialValue = new Date(this.data.resource[field.name]);
      }
      
      formControls[field.name] = [initialValue, validators];
    });

    this.form = this.fb.group(formControls);
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
