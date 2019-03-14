/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { CensorshipDetectorTestModule } from '../../../test.module';
import { WebAddressUpdateComponent } from 'app/entities/web-address/web-address-update.component';
import { WebAddressService } from 'app/entities/web-address/web-address.service';
import { WebAddress } from 'app/shared/model/web-address.model';

describe('Component Tests', () => {
    describe('WebAddress Management Update Component', () => {
        let comp: WebAddressUpdateComponent;
        let fixture: ComponentFixture<WebAddressUpdateComponent>;
        let service: WebAddressService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CensorshipDetectorTestModule],
                declarations: [WebAddressUpdateComponent]
            })
                .overrideTemplate(WebAddressUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(WebAddressUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(WebAddressService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new WebAddress(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.webAddress = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new WebAddress();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.webAddress = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
