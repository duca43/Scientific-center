import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ScientificAreasService {

  constructor(private http: HttpClient) { }

  getScientificAreas() {
    return this.http.get('/api/scientific_areas');
  }
}
