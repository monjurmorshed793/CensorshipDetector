import { Routes } from '@angular/router';
import { fileUploaderRoute } from 'app/core/file-uploader/file-uploader.route';

const CORE_ROUTES = [fileUploaderRoute];

export const coreState: Routes = [
    {
        path: '',
        children: CORE_ROUTES
    }
];
