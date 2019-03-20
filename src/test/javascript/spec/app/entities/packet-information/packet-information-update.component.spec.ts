/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { CensorshipDetectorTestModule } from '../../../test.module';
import { PacketInformationUpdateComponent } from 'app/entities/packet-information/packet-information-update.component';
import { PacketInformationService } from 'app/entities/packet-information/packet-information.service';
import { PacketInformation } from 'app/shared/model/packet-information.model';

describe('Component Tests', () => {
    describe('PacketInformation Management Update Component', () => {
        let comp: PacketInformationUpdateComponent;
        let fixture: ComponentFixture<PacketInformationUpdateComponent>;
        let service: PacketInformationService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CensorshipDetectorTestModule],
                declarations: [PacketInformationUpdateComponent]
            })
                .overrideTemplate(PacketInformationUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(PacketInformationUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PacketInformationService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new PacketInformation(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.packetInformation = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new PacketInformation();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.packetInformation = entity;
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
